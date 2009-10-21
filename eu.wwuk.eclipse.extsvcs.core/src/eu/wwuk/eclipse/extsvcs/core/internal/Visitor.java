package eu.wwuk.eclipse.extsvcs.core.internal;

public interface Visitor<T> {
	void visit(T object) throws Exception;
}
