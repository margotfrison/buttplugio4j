package me.margotfrison.buttplugio4j.client.simplified;

public class SimplePromise<T> extends AbstractPromise<T> {
	public SimplePromise() { }
	
	@Override
	public void resolve(T result) {
		super.resolve(result);
		done = true;
	}
	
	@Override
	public void error(Exception error) {
		super.error(error);
		done = true;
	}
}
