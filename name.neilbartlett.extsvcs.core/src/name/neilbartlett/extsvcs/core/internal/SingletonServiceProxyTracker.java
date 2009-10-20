package name.neilbartlett.extsvcs.core.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.SortedMap;
import java.util.TreeMap;

import name.neilbartlett.extsvcs.core.ServiceUnavailableException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class SingletonServiceProxyTracker extends ServiceTracker implements InvocationHandler {
	
	private Object currentService = null;
	private SortedMap<ServiceReference, Object> rankedServices
		= new TreeMap<ServiceReference, Object>();
	
	public SingletonServiceProxyTracker(BundleContext context, Filter filter) {
		super(context, filter, null);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object target = null;
		synchronized(this) {
			target = currentService;
		}
		
		if(target == null)
			throw new ServiceUnavailableException(filter.toString());
		
		return method.invoke(target, args);
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		Object service = context.getService(reference);
		
		synchronized(this) {
			rankedServices.put(reference, service);
			if(currentService == null)
				currentService = service;
		}
		
		return service;
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		synchronized (this) {
			rankedServices.remove(reference);
			if(service == currentService) {
				if(rankedServices.isEmpty()) {
					currentService = null;
				} else {
					ServiceReference highest = rankedServices.lastKey();
					currentService = rankedServices.get(highest);
				}
			}
		}
		
		context.ungetService(reference);
	}

}
