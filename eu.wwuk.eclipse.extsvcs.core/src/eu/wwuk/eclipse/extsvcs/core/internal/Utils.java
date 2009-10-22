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

import org.osgi.framework.Constants;

public class Utils {
	public static final String buildFilter(String objectClass, String extraFilter) {
		String result;
		if(extraFilter != null && extraFilter.length() > 0) {
			result = String.format("(&(%s=%s)%s)",
					Constants.OBJECTCLASS, objectClass,
					extraFilter);
		} else {
			result = String.format("(%s=%s)",
					Constants.OBJECTCLASS, objectClass);
		}
		return result;
	}

	public static String simpleClassName(Class<?> clazz) {
		Package pkg = clazz.getPackage();
		String result;
		if(pkg == null)
			result = clazz.getName();
		else
			result = clazz.getName().substring(pkg.getName().length() + 1);
		return result;
	}
}
