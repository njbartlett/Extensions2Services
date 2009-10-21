package name.neilbartlett.extsvcs.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionBinder implements Binder {
	
	private final String bindMethodName;
	private final String unbindMethodName;
	private final Class<?> serviceClass;

	public ReflectionBinder(String bindMethodName, String unbindMethodName, Class<?> serviceClass) {
		this.bindMethodName = bindMethodName;
		this.unbindMethodName = unbindMethodName;
		this.serviceClass = serviceClass;
	}

	public void bind(Object target, Object service) throws BindingException {
		try {
			Method bindMethod = target.getClass().getDeclaredMethod(bindMethodName, serviceClass);
			if(target != null) bindMethod.invoke(target, service);
		} catch (IllegalArgumentException e) {
			throw new BindingException(e);
		} catch (IllegalAccessException e) {
			throw new BindingException(e);
		} catch (InvocationTargetException e) {
			throw new BindingException(e);
		} catch (SecurityException e) {
			throw new BindingException(e);
		} catch (NoSuchMethodException e) {
			throw new BindingException(e);
		}
	}

	public void unbind(Object target, Object service) throws BindingException {
		try {
			Method unbindMethod = target.getClass().getDeclaredMethod(unbindMethodName, serviceClass);
			if(target != null) unbindMethod.invoke(target, service);
		} catch (IllegalArgumentException e) {
			throw new BindingException(e);
		} catch (IllegalAccessException e) {
			throw new BindingException(e);
		} catch (InvocationTargetException e) {
			throw new BindingException(e);
		} catch (SecurityException e) {
			throw new BindingException(e);
		} catch (NoSuchMethodException e) {
			throw new BindingException(e);
		}
	}

}
