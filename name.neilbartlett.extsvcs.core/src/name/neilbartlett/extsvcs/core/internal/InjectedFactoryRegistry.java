package name.neilbartlett.extsvcs.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;


public class InjectedFactoryRegistry extends BundleTracker {
	
	public InjectedFactoryRegistry(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
	}
	
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		Map<String, InjectedFactory> factoriesById = null;
		
		IContributor contributor = ContributorFactoryOSGi.createContributor(bundle);
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensions(contributor);
		
		String extPointId = Activator.PLUGIN_ID + "." + Activator.EXT_INJECTED_FACTORIES;
		for (IExtension extension : extensions) {
			if(extPointId.equals(extension.getExtensionPointUniqueIdentifier())) {
				IConfigurationElement[] factoryElements = extension.getConfigurationElements();
				if(factoryElements.length > 0) {
					if(factoriesById == null)
						factoriesById = new HashMap<String, InjectedFactory>();
					for (IConfigurationElement factoryElement : factoryElements) {
						InjectedFactory factory = new InjectedFactory(bundle, factoryElement);
						factoriesById.put(factory.getFactoryId(), factory);
						factory.openReferenceTrackers();
					}
				}
			}
		}
		
		return factoriesById;
	}

	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
	}

	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
	}
}
