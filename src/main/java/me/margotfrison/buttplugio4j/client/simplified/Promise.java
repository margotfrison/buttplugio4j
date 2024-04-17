package me.margotfrison.buttplugio4j.client.simplified;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Promise<T> extends Future<T> {
	public Promise<T> then(Consumer<T> callback);
	public Promise<T> ifError(Consumer<Exception> errorCallback);
	public void resolve(T result);
	public void error(Exception error);
}
