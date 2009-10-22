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
package eu.wwuk.eclipse.extsvcs.core;

import static eu.wwuk.eclipse.extsvcs.core.internal.Constants.EXT_INJECTED_FACTORIES;
import static eu.wwuk.eclipse.extsvcs.core.internal.Constants.PLUGIN_ID;

import java.util.Dictionary;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import eu.wwuk.eclipse.extsvcs.core.internal.Activator;
import eu.wwuk.eclipse.extsvcs.core.internal.InjectedFactory;

/**
 * A factory for service-injected components. This class must not be
 * instantiated directly from Java code, it is intended to be used in the
 * <code>class</code> attribute of an extension declaration along with
 * additional data indicating the factory ID to access. For example:
 * 
 * <pre>
 * 	&lt;extension
	      point="org.eclipse.ui.views"&gt;
	   &lt;view
	         id="org.example.views.logView"
	         class="<strong>eu.wwuk.eclipse.extsvcs.core.InjectionFactory</strong>:logReaderView"
	   &lt;/view&gt;
	&lt;/extension&gt;

 * </pre>
 * 
 * @author Neil Bartlett
 * 
 */
public class InjectionFactory implements IExecutableExtension,
		IExecutableExtensionFactory {
	
	private static final String ATTR_ID = "id";
	private static final Object PROP_FACTORY_ID = "factoryId";
	
	private IConfigurationElement config;
	private String propertyName;
	private String factoryId = null;

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
		this.propertyName = propertyName;
		
		if(data == null)
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "Factory ID must be specified", null));
		if(data instanceof String) {
			factoryId = (String) data;
		} else if(data instanceof Dictionary<?, ?>) {
			@SuppressWarnings("unchecked")
			Dictionary<String, String> dict = (Dictionary<String, String>) data;
			factoryId = dict.get(PROP_FACTORY_ID);
			if(factoryId == null)
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, "A factoryId parameter must be specified", null));
		}
	}

	public Object create() throws CoreException {
		String contribBundleId = config.getContributor().getName();
		
		// 1. Find the injectedFactory element
		IConfigurationElement matchingFactoryElement = null;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(PLUGIN_ID, EXT_INJECTED_FACTORIES);
		for (IConfigurationElement element : elements) {
			if(contribBundleId.equals(element.getContributor().getName())) {
				String id = element.getAttribute(ATTR_ID);
				if(factoryId.equals(id)) {
					matchingFactoryElement = element;
					break;
				}
			}
		}
		if(matchingFactoryElement == null) throwError("Unable to find factory with ID '" + factoryId + "' in contributing bundle '" + contribBundleId + "'.");
		
		// 2. Find and start the bundle. This will cause the bundle tracker to pick up the bundle if it has an extension to the
		//    injectedFactories extpoint.
		Bundle bundle = Activator.findBundleBySymbolicName(contribBundleId);
		if(bundle == null) throwError("Failed to find bundle for contributor '" + contribBundleId + "'.");
		try {
			bundle.start(Bundle.START_TRANSIENT);
		} catch (BundleException e) {
			throwError("Failed to start bundle '" + contribBundleId + "'.", e);
		}
		
		// 3. Lookup the injected factory ID
		@SuppressWarnings("unchecked")
		Map<String, InjectedFactory> factoriesById = (Map<String, InjectedFactory>) Activator.getFactoryRegistry().getObject(bundle);
		InjectedFactory factory = (factoriesById != null) ? factoriesById.get(factoryId) : null;
		if(factory == null) throwError("Cannot find an injectedFactory extension with the ID '" + factoryId + "' in bundle '" + contribBundleId + "'.");
		
		return factory.createInstance();
	}

	void throwError(String message) throws CoreException {
		throwError(message, null);
	}

	void throwError(String message, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, exception));
	}

}
