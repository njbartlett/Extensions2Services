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

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * An optional interface that may be implemented by extension objects
 * instantiated by an injected factory. Immediately after the construction of an
 * object, the factory checks if it implements this interface and, if it does,
 * calls the <code>setComponentContext</code> method to supply the context for
 * the instance.
 * 
 * @author Neil Bartlett
 * @see ComponentContext
 * @see IExecutableExtension
 * @see IExecutableExtensionFactory
 */
public interface InjectedComponent {
	/**
	 * Sets the component context. Note that if the extension class
	 * <strong>also</strong> implements
	 * <code>org.eclipse.core.runtime.IExecutableExtension</code> and/or
	 * <code>org.eclipse.core.runtime.IExecutableExtensionFactory</code> then
	 * this method will be called <strong>after</strong>
	 * <code>IExecutableExtension.setInitializationData()</code> and after
	 * <code>IExecutableExtension.create()</code>.
	 * 
	 * @param context The component context.
	 * @see ComponentContext
	 * @see IExecutableExtension
	 * @see IExecutableExtensionFactory
	 */
	void setComponentContext(ComponentContext context);
}
