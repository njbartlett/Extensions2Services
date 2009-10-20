package name.neilbartlett.extsvcs.core;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import name.neilbartlett.extsvcs.core.internal.Activator;
import name.neilbartlett.extsvcs.core.internal.InjectedFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

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
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Factory ID must be specified", null));
		if(data instanceof String) {
			factoryId = (String) data;
		} else if(data instanceof Dictionary<?, ?>) {
			@SuppressWarnings("unchecked")
			Dictionary<String, String> dict = (Dictionary<String, String>) data;
			factoryId = dict.get(PROP_FACTORY_ID);
			if(factoryId == null)
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "A factoryId parameter must be specified", null));
		}
	}

	public Object create() throws CoreException {
		String contribBundleId = config.getContributor().getName();
		
		// 1. Find the injectedFactory element
		IConfigurationElement matchingFactoryElement = null;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID, Activator.EXT_INJECTED_FACTORIES);
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
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, exception));
	}

}
