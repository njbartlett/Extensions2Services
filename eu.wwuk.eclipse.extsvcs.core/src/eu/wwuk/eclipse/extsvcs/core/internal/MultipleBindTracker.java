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

import java.lang.reflect.InvocationTargetException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class MultipleBindTracker extends BindingTracker {
	
	private final WeakCollection<Object> bindTargets = new WeakCollection<Object>();
	
	public MultipleBindTracker(Binder binder, BundleContext context, Filter filter) {
		super(binder, context, filter, null);
	}
	
	@Override
	public void addBindingTarget(Object target) throws BindingException {
		bindTargets.add(target);
		Object[] services = getServices();
		for (Object service : services) {
			binder.bind(target, service);
		}
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		final Object service = super.addingService(reference);
		try {
			bindTargets.accept(new Visitor<Object> () {
				public void visit(Object target) throws BindingException {
					binder.bind(target, service);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return service;
	}
	
	@Override
	public void removedService(ServiceReference reference, final Object service) {
		super.removedService(reference, service);
		try {
			bindTargets.accept(new Visitor<Object> () {
				public void visit(Object target) throws BindingException {
					binder.unbind(target, service);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
