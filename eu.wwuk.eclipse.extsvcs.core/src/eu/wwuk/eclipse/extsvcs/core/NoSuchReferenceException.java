package eu.wwuk.eclipse.extsvcs.core;

public class NoSuchReferenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchReferenceException(String name) {
		super(name);
	}
}