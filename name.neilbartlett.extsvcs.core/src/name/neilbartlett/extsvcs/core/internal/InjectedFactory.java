package name.neilbartlett.extsvcs.core.internal;

import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_FACTORY_CLASS;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_FACTORY_ID;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_BIND;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_CARDINALITY;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_FILTER;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_INTERFACE;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_UNBIND;
import static name.neilbartlett.extsvcs.core.internal.Constants.ELEM_REFERENCE;
import static name.neilbartlett.extsvcs.core.internal.Constants.PLUGIN_ID;
import static name.neilbartlett.extsvcs.core.internal.Constants.VAL_REFERENCE_CARDINALITY_MULTIPLE;
import static name.neilbartlett.extsvcs.core.internal.Constants.VAL_REFERENCE_CARDINALITY_SINGLE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.neilbartlett.extsvcs.core.InjectedComponent;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class InjectedFactory {
	
	private static final String PREFIX_BIND_MULTIPLE = "add";
	private static final String PREFIX_UNBIND_MULTIPLE = "remove";
	private static final String PREFIX_BIND_SINGLE = "set";
	private static final String PREFIX_UNBIND_SINGLE = "unset";
	
	private final IConfigurationElement factoryElement;
	
	private final Map<IConfigurationElement, BindingTracker> trackers = new ConcurrentHashMap<IConfigurationElement, BindingTracker>();
	private final ComponentContextImpl context;

	public InjectedFactory(Bundle bundle, IConfigurationElement factoryElement) {
		this.factoryElement = factoryElement;
		
		IConfigurationElement[] refElements = factoryElement.getChildren(ELEM_REFERENCE);
		for (IConfigurationElement refElement : refElements) {
			try {
				String interfaceName = refElement.getAttribute(ATTR_REFERENCE_INTERFACE);
				String extraFilter = refElement.getAttribute(ATTR_REFERENCE_FILTER);
				Filter filter = FrameworkUtil.createFilter(Utils.buildFilter(interfaceName, extraFilter));
				
				Class<?> serviceClass = bundle.loadClass(interfaceName);
				String simpleInterfaceName = Utils.simpleClassName(serviceClass);

				String cardinality = refElement.getAttribute(ATTR_REFERENCE_CARDINALITY);
				String bindMethodName = refElement.getAttribute(ATTR_REFERENCE_BIND);
				String unbindMethodName = refElement.getAttribute(ATTR_REFERENCE_UNBIND);
				
				// Calculate default bind and unbind method names if not provided
				if(VAL_REFERENCE_CARDINALITY_MULTIPLE.equals(cardinality)) {
					if(bindMethodName == null)
						bindMethodName = PREFIX_BIND_MULTIPLE + simpleInterfaceName;
					if(unbindMethodName == null)
						unbindMethodName = PREFIX_UNBIND_MULTIPLE + simpleInterfaceName;
				} else {
					if(bindMethodName == null)
						bindMethodName = PREFIX_BIND_SINGLE + simpleInterfaceName;
					if(unbindMethodName == null)
						unbindMethodName = PREFIX_UNBIND_SINGLE + simpleInterfaceName;
				}

				ReflectionBinder binder = new ReflectionBinder(bindMethodName, unbindMethodName, serviceClass);
				
				String cardinalityStr = refElement.getAttribute(ATTR_REFERENCE_CARDINALITY);
				if(cardinalityStr == null || VAL_REFERENCE_CARDINALITY_SINGLE.equals(cardinalityStr)) {
					BindingTracker tracker = new SingletonBindTracker(binder, bundle.getBundleContext(), filter);
					trackers.put(refElement, tracker);
				} else if(VAL_REFERENCE_CARDINALITY_MULTIPLE.equals(cardinalityStr)) {
					BindingTracker tracker = new MultipleBindTracker(binder, bundle.getBundleContext(), filter);
					trackers.put(refElement, tracker);
				}
			} catch (InvalidSyntaxException e) {
				// TODO
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		context = new ComponentContextImpl(trackers);
	}
 	
	public void openReferenceTrackers() {
		for (ServiceTracker tracker : trackers.values()) {
			tracker.open();
		}
	}
	
	public Object createInstance() throws CoreException {
		// 1. Create the object
		Object object = factoryElement.createExecutableExtension(ATTR_FACTORY_CLASS);
		
		// 2. Set the component context if the object implements the right interface to receive it
		if(object instanceof InjectedComponent)
			((InjectedComponent) object).setComponentContext(context);
		
		// 3. Add the new instance to the trackers; this will cause the bind methods to be called with the current
		//    bound service(s).
		for (BindingTracker tracker : trackers.values()) {
			try {
				tracker.addBindingTarget(object);
			} catch (BindingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return object;
	}
	
	public String getFactoryId() {
		return factoryElement.getAttribute(ATTR_FACTORY_ID);
	}
	
	void callSetter(Object target, String methodName, Class<?> objectClass, Object value) throws CoreException {
		try {
			Method method = target.getClass().getDeclaredMethod(methodName, objectClass);
			method.invoke(target, value);
		} catch (SecurityException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Security error invoking setter method '" + methodName + "'.", e));
		} catch (IllegalArgumentException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Illegal argument error invoking setter method '" + methodName + "'.", e));
		} catch (NoSuchMethodException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Setter method does not exist or has wrong parameter type: '" + methodName + "'.", e));
		} catch (IllegalAccessException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Illegal access error invoking setter method '" + methodName + "'.", e));
		} catch (InvocationTargetException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "An exception was thrown by the setter method '" + methodName + "'.", e));
		}
	}
}
