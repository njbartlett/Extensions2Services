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

public class Constants {
	public static final String PLUGIN_ID = "eu.wwuk.eclipse.extsvcs.core";
	public static final String EXT_INJECTED_FACTORIES = "injectedFactories";

	static final String ATTR_FACTORY_ID = "id";
	static final String ATTR_FACTORY_CLASS = "class";
	
	static final String ELEM_PROPERTY = "property";
	static final String ATTR_PROPERTY_NAME = "name";
	static final String ATTR_PROPERTY_VALUE = "value";
	
	static final String ELEM_SERVICE = "service";
	static final String ELEM_PROVIDE = "provide";
	static final String ATTR_PROVIDE_INTERFACE = "interface";
	
	static final String ELEM_REFERENCE = "reference";
	static final String ATTR_REFERENCE_NAME = "name";
	static final String ATTR_REFERENCE_INTERFACE = "interface";
	static final String ATTR_REFERENCE_FILTER = "filter";
	static final String ATTR_REFERENCE_CARDINALITY = "cardinality";
	static final String ATTR_REFERENCE_BIND = "bind";
	static final String ATTR_REFERENCE_UNBIND = "unbind";
	
	static final String VAL_REFERENCE_CARDINALITY_SINGLE = "single";
	static final String VAL_REFERENCE_CARDINALITY_MULTIPLE = "multiple";
}
