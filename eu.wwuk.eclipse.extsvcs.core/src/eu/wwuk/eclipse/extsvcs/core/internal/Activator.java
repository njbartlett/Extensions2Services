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
