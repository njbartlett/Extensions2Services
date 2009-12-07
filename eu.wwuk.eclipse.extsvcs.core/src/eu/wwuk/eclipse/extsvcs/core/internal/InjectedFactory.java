/*******************************************************************************
 * Copyright (c) 2009 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
package eu.wwuk.eclipse.extsvcs.core.internal;

import static eu.wwuk.eclipse.extsvcs.core.internal.Constants.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.wwuk.eclipse.extsvcs.core.InjectedComponent;

public class InjectedFactory {
	
	private static final String PREFIX_BIND_MULTIPLE = "add";
	private static final String PREFIX_UNBIND_MULTIPLE = "remove";
	private static final String PREFIX_BIND_SINGLE = "set";
	private static final String PREFIX_UNBIND_SINGLE = "unset";
	
	private final Bundle bundle;
	private final IConfigurationElement factoryElement;

	private final Properties properties = new Properties();
	private final List<String> provides = new ArrayList<String>();
	private final Map<IConfigurationElement, BindingTracker> trackers = new ConcurrentHashMap<IConfigurationElement, BindingTracker>();

	public InjectedFactory(Bundle bundle, IConfigurationElement factoryElement) {
		this.bundle = bundle;
		this.factoryElement = factoryElement;
		
		IConfigurationElement[] propertyElems = factoryElement.getChildren(ELEM_PROPERTY);
		for (IConfigurationElement propertyElem : propertyElems) {
			String name = propertyElem.getAttribute(ATTR_PROPERTY_NAME);
			String value = propertyElem.getAttribute(ATTR_PROPERTY_VALUE);
			properties.setProperty(name, value != null ? value : "");
		}
		
		IConfigurationElement[] svcElements = factoryElement.getChildren(ELEM_SERVICE);
		for (IConfigurationElement svcElement : svcElements) {
			IConfigurationElement[] provideElems = svcElement.getChildren(ELEM_PROVIDE);
			for (IConfigurationElement provideElem : provideElems) {
				String provideInterface = provideElem.getAttribute(ATTR_PROVIDE_INTERFACE);
				provides.add(provideInterface);
			}
		}
		
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
	}
 	
	public void openReferenceTrackers() {
		for (ServiceTracker tracker : trackers.values()) {
			tracker.open();
		}
	}
	
	public Object createInstance() throws CoreException {
		// 1. Create the object and component context
		Object object = factoryElement.createExecutableExtension(ATTR_FACTORY_CLASS);
		
		// 2. Publish any services listed in the <provides> elements
		ServiceRegistration registration = null;
		if(provides != null && !provides.isEmpty()) {
			String[] providesArray = provides.toArray(new String[provides.size()]);
			registration = bundle.getBundleContext().registerService(providesArray, object, properties); 
		}
		
		// 3. Set the component context if the object implements the right interface to receive it
		if(object instanceof InjectedComponent) {
			ComponentContextImpl context = new ComponentContextImpl(trackers, registration);
			((InjectedComponent) object).setComponentContext(context);
		}
		
		// 4. Add the new instance to the trackers; this will cause the bind methods to be called with the current
		//    bound service(s).
		for (BindingTracker tracker : trackers.values()) {
			try {
				tracker.addBindingTarget(object);
			} catch (BindingException e) {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Binding error occurred adding new instance to trackers.", e));
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
