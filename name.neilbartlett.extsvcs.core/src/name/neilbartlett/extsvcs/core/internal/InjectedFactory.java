package name.neilbartlett.extsvcs.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class InjectedFactory {
	private static final String ATTR_FACTORY_ID = "id";
	private static final String ATTR_FACTORY_CLASS = "class";
	
	private static final String ELEM_REFERENCE = "reference";
	private static final String ATTR_REFERENCE_INTERFACE = "interface";
	private static final String ATTR_REFERENCE_FILTER = "filter";
	private static final String ATTR_REFERENCE_CARDINALITY = "cardinality";
	private static final String ATTR_REFERENCE_SETTER = "setter";
	
	private static final String VAL_REFERENCE_CARDINALITY_SINGLE = "single";
	private static final String VAL_REFERENCE_CARDINALITY_MULTIPLE = "single";
	
	private final IConfigurationElement factoryElement;
	private final Bundle bundle;
	
	private final Map<IConfigurationElement, SingletonServiceProxyTracker> singletonTrackers = new ConcurrentHashMap<IConfigurationElement, SingletonServiceProxyTracker>();

	public InjectedFactory(Bundle bundle, IConfigurationElement factoryElement) {
		this.bundle = bundle;
		this.factoryElement = factoryElement;
	}
 	
	public void openReferenceTrackers() {
		IConfigurationElement[] refElements = factoryElement.getChildren(ELEM_REFERENCE);
		for (IConfigurationElement refElement : refElements) {
			try {
				String objectClass = refElement.getAttribute(ATTR_REFERENCE_INTERFACE);
				String extraFilter = refElement.getAttribute(ATTR_REFERENCE_FILTER);
				Filter filter = FrameworkUtil.createFilter(Utils.buildFilter(objectClass, extraFilter));
				
				String cardinalityStr = refElement.getAttribute(ATTR_REFERENCE_CARDINALITY);
				if(cardinalityStr == null || VAL_REFERENCE_CARDINALITY_SINGLE.equals(cardinalityStr)) {
					SingletonServiceProxyTracker tracker = new SingletonServiceProxyTracker(bundle.getBundleContext(), filter);
					singletonTrackers.put(refElement, tracker);
					tracker.open();
				} else if(VAL_REFERENCE_CARDINALITY_MULTIPLE.equals(cardinalityStr)) {
					// TODO
				}
			} catch (InvalidSyntaxException e) {
				// TODO
			}
		}
	}
	
	public Object createInstance() throws CoreException {
		Object object = factoryElement.createExecutableExtension(ATTR_FACTORY_CLASS);
		
		for (Entry<IConfigurationElement, SingletonServiceProxyTracker> entry : singletonTrackers.entrySet()) {
			IConfigurationElement refElement = entry.getKey();
			String interfaceName = refElement.getAttribute(ATTR_REFERENCE_INTERFACE);
			String setterName = refElement.getAttribute(ATTR_REFERENCE_SETTER);
			
			try {
				Class<?> objectClass = bundle.loadClass(interfaceName);
				if(!objectClass.isInterface()) throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Specified reference class is not an interface: '" + interfaceName + "'.", null));
				
				Object proxy = Proxy.newProxyInstance(BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle), new Class<?>[] { objectClass }, entry.getValue());
				callSetter(object, setterName, objectClass, proxy);
			} catch (ClassNotFoundException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Reference interface name '" + interfaceName + "' could not be loaded by the bundle classloader for bundle " + bundle.getLocation(), e));
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
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Security error invoking setter method '" + methodName + "'.", e));
		} catch (IllegalArgumentException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Illegal argument error invoking setter method '" + methodName + "'.", e));
		} catch (NoSuchMethodException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Setter method does not exist or has wrong parameter type: '" + methodName + "'.", e));
		} catch (IllegalAccessException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Illegal access error invoking setter method '" + methodName + "'.", e));
		} catch (InvocationTargetException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "An exception was thrown by the setter method '" + methodName + "'.", e));
		}
	}
}
