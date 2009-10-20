package name.neilbartlett.extsvcs.core.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testBuildFilter1() {
		assertEquals("(objectClass=org.foo.Bar)", Utils.buildFilter("org.foo.Bar", null));
	}
	
	@Test
	public void testBuildFilter2() {
		assertEquals("(&(objectClass=org.foo.Bar)(lang=en_GB))", Utils.buildFilter("org.foo.Bar", "(lang=en_GB)"));
	}
}
