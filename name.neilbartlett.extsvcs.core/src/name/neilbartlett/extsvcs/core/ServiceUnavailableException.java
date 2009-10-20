package name.neilbartlett.extsvcs.core;

public class ServiceUnavailableException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final String filter;

	public ServiceUnavailableException(String filter) {
		this.filter = filter;
	}
	
	public String getFilter() {
		return filter;
	}
}
