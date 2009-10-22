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

public class BindingException extends Exception {

	private static final long serialVersionUID = 1L;

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}

	public BindingException(String message) {
		super(message);
	}

	public BindingException(Throwable cause) {
		super(cause);
	}

}
