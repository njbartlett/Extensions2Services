package name.neilbartlett.extsvcs.core.internal;

public class BindingException extends Exception {

	private static final long serialVersionUID = 1L;

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}

	public BindingException(String message) {
		super(message);
	}

	public BindingException(Throwable cause) {
		super(cause);
	}

}
