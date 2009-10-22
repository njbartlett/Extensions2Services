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

/**
 * The context of a component instantiated by an injected factory.
 * 
 * @author Neil Bartlett
 * @see InjectedComponent
 */
public interface ComponentContext {
	/**
	 * Locate a service by name. Note that the returned service object should be
	 * used only briefly and then allowed to pass out of scope. It should
	 * <em>not</em> be stored in a field or collection for later use.
	 * 
	 * @param name
	 *            The reference name, which corresponds to the value of the
	 *            <code>name</code> specified on the <code>reference</code>
	 *            element, or the <code>interface</code> attribute if
	 *            <code>name</code> is not defined
	 * @return The service, or <code>null</code> if the service is currently
	 *         unavailable or if no reference exists with the specified name.
	 */
	Object locateService(String name);
}
