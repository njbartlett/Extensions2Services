package eu.wwuk.eclipse.extsvcs.core.internal;

public interface Binder {
	void bind(Object target, Object service) throws BindingException;
	void unbind(Object target, Object service) throws BindingException;
}
