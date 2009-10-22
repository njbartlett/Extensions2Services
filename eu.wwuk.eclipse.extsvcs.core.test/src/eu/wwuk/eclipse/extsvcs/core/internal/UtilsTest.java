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

import static org.junit.Assert.*;

import org.junit.Test;

import eu.wwuk.eclipse.extsvcs.core.internal.Utils;

public class UtilsTest {

	@Test
	public void testBuildFilter1() {
		assertEquals("(objectClass=org.foo.Bar)", Utils.buildFilter("org.foo.Bar", null));
	}
	
	@Test
	public void testBuildFilter2() {
		assertEquals("(&(objectClass=org.foo.Bar)(lang=en_GB))", Utils.buildFilter("org.foo.Bar", "(lang=en_GB)"));
	}
	
	@Test
	public void testSimpleClassName() throws Exception {
		assertEquals("String", Utils.simpleClassName(String.class));
	}
}
