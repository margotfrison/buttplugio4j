package me.margotfrison.buttplugio4j.client.simplified;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * A promise. A way to set callbacks that will be called when
 * the object computed asynchronously will be available.
 * @param <T> the type of the {@link Object} handled by
 * the promise
 */
public interface Promise<T> extends Future<T> {
	public Promise<T> then(Consumer<T> callback);
	public Promise<T> ifError(Consumer<Exception> errorCallback);
	public void resolve(T result);
	public void error(Exception error);
}
