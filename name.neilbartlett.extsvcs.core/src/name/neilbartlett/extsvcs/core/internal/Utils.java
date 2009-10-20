package name.neilbartlett.extsvcs.core.internal;

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
}
