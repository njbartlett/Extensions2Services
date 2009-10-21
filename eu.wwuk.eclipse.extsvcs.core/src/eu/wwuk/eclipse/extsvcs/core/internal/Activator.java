package eu.wwuk.eclipse.extsvcs.core.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private volatile static BundleContext context = null;
	private volatile static InjectedFactoryRegistry factoryRegistry = null;
	
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.factoryRegistry = new InjectedFactoryRegistry(context);
		Activator.factoryRegistry.open();
	}

	public void stop(BundleContext context) throws Exception {
		Activator.factoryRegistry.close();
		Activator.context = null;
	}
	
	public static InjectedFactoryRegistry getFactoryRegistry() {
		return Activator.factoryRegistry;
	}
	
	public static Bundle findBundleBySymbolicName(String symbolicName) {
		Bundle result = null;
		
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if(bundle.getSymbolicName().equals(symbolicName)) {
				result = bundle;
				break;
			}
		}
		
		return result;
	}

}
