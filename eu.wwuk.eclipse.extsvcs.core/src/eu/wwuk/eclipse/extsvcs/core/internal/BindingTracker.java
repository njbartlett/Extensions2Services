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

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class BindingTracker extends ServiceTracker {

	protected final Binder binder;

	public BindingTracker(Binder binder, BundleContext context, Filter filter,
			ServiceTrackerCustomizer customizer) {
		super(context, filter, customizer);
		this.binder = binder;
	}

	public BindingTracker(Binder binder, BundleContext context, ServiceReference reference,
			ServiceTrackerCustomizer customizer) {
		super(context, reference, customizer);
		this.binder = binder;
	}

	public BindingTracker(Binder binder, BundleContext context, String clazz,
			ServiceTrackerCustomizer customizer) {
		super(context, clazz, customizer);
		this.binder = binder;
	}
	
	public abstract void addBindingTarget(Object target) throws BindingException;
}
