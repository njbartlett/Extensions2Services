package eu.wwuk.eclipse.extsvcs.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class SingletonBindTracker extends BindingTracker {
	
	private final AtomicReference<Object> boundServiceRef = new AtomicReference<Object>(null);
	private final WeakCollection<Object> bindTargets = new WeakCollection<Object>();
	
	public SingletonBindTracker(Binder binder, BundleContext context, Filter filter) {
		super(binder, context, filter, null);
	}
	
	@Override
	public void addBindingTarget(Object target) throws BindingException {
		bindTargets.add(target);
		Object boundService = boundServiceRef.get();
		if(boundService != null) binder.bind(target, boundService);
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		final Object service = super.addingService(reference);
		
		boolean shouldBind = boundServiceRef.compareAndSet(null, service);
		if(shouldBind) {
			try {
				bindTargets.accept(new Visitor<Object>() {
					public void visit(Object target) throws BindingException {
						binder.bind(target, service);
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return service;
	}
	
	@Override
	public void removedService(ServiceReference reference, final Object service) {
		super.removedService(reference, service);
		final Object replacement = getService();
		
		boolean shouldUnbind = boundServiceRef.compareAndSet(service, replacement);
		if(shouldUnbind) {
			try {
				bindTargets.accept(new Visitor<Object>() {
					public void visit(Object target) throws BindingException {
						binder.unbind(target, service);
						if(replacement != null) binder.bind(target, replacement);
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
