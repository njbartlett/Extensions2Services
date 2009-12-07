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

import static eu.wwuk.eclipse.extsvcs.core.internal.Constants.ATTR_REFERENCE_INTERFACE;
import static eu.wwuk.eclipse.extsvcs.core.internal.Constants.ATTR_REFERENCE_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.wwuk.eclipse.extsvcs.core.ComponentContext;
import eu.wwuk.eclipse.extsvcs.core.NoSuchReferenceException;

public class ComponentContextImpl implements ComponentContext {
	
	final Map<String, ServiceTracker> trackerMap = new HashMap<String, ServiceTracker>();
	final ServiceRegistration registration;
	
	public ComponentContextImpl(Map<IConfigurationElement, ? extends ServiceTracker> references, ServiceRegistration registration) {
		this.registration = registration;
		for (Entry<IConfigurationElement, ? extends ServiceTracker> entry : references.entrySet()) {
			IConfigurationElement element = entry.getKey();
			String name = element.getAttribute(ATTR_REFERENCE_NAME);
			if(name == null || name.length() == 0) {
				name = element.getAttribute(ATTR_REFERENCE_INTERFACE);
			}
			if(name != null)
				trackerMap.put(name, entry.getValue());
		}
	}

	public Object locateService(String name) throws NoSuchReferenceException {
		ServiceTracker serviceTracker = trackerMap.get(name);
		if(serviceTracker == null)
			throw new NoSuchReferenceException(name);
		return serviceTracker.getService();
	}

	public void disposed() {
		if(registration != null)
			registration.unregister();
	}
}
