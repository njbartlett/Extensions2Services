package name.neilbartlett.extsvcs.core.internal;

import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_INTERFACE;
import static name.neilbartlett.extsvcs.core.internal.Constants.ATTR_REFERENCE_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import name.neilbartlett.extsvcs.core.ComponentContext;

import org.eclipse.core.runtime.IConfigurationElement;
import org.osgi.util.tracker.ServiceTracker;

public class ComponentContextImpl implements ComponentContext {
	
	final Map<String, ServiceTracker> trackerMap = new HashMap<String, ServiceTracker>();
	
	public ComponentContextImpl(Map<IConfigurationElement, ? extends ServiceTracker> references) {
		for (Entry<IConfigurationElement, ? extends ServiceTracker> entry : references.entrySet()) {
			IConfigurationElement element = entry.getKey();
			String name = element.getAttribute(ATTR_REFERENCE_NAME);
			if(name == null || name.length() == 0) {
				name = element.getAttribute(ATTR_REFERENCE_INTERFACE);
			}
			if(name != null)
				trackerMap.put(name, entry.getValue());
		}
	}

	public Object locateService(String name) {
		ServiceTracker serviceTracker = trackerMap.get(name);
		return serviceTracker.getService();
	}
}
