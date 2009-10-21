package name.neilbartlett.extsvcs.core.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import name.neilbartlett.extsvcs.core.ServiceUnavailableException;

import org.osgi.util.tracker.ServiceTracker;

public class ServiceInvocationHandler implements InvocationHandler {
	
	private final String filter;
	private final ServiceTracker tracker;
	private final long timeout;
	
	public ServiceInvocationHandler(String filter, ServiceTracker tracker) {
		this(filter, tracker, 0);
	}

	public ServiceInvocationHandler(String filter, ServiceTracker tracker, long timeout) {
		this.filter = filter;
		this.tracker = tracker;
		this.timeout = timeout;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object service = null;
		if(timeout == 0) {
			service = tracker.getService();
		} else if(timeout < 0) {
			service = tracker.waitForService(0);
		} else {
			service = tracker.waitForService(timeout);
		}
		
		if(service == null)
			throw new ServiceUnavailableException(filter);
		
		return method.invoke(service, args);

	}
}
