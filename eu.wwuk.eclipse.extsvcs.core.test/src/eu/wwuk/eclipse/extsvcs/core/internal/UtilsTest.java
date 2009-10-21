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
