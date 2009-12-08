package eu.wwuk.eclipse.extsvcs.core.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class WeakServiceRegistration<T> extends WeakReference<T> {

	private final ServiceRegistration registration;

	public WeakServiceRegistration(ServiceRegistration registration, T referent, ReferenceQueue<? super T> q) {
		super(referent, q);
		this.registration = registration;
	}
	
}
