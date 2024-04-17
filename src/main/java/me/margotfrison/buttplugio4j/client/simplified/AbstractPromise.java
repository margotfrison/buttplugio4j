package me.margotfrison.buttplugio4j.client.simplified;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import lombok.NonNull;

public abstract class AbstractPromise<T> implements Promise<T> {
	private static final long FUTURE_CHECK_DELAY = 10;

	protected Consumer<T> callback;
	protected Consumer<Exception> errorCallback;
	protected boolean cancelled = false;
	protected boolean done = false;
	private List<T> resultQueue = new ArrayList<>();
	private List<Exception> errorQueue = new ArrayList<>();

	public AbstractPromise() { }

	@NonNull
	public AbstractPromise<T> then(Consumer<T> callback) {
		this.callback = callback;
		resultQueue.forEach(r -> callback.accept(r));
		return this;
	}

	@NonNull
	public AbstractPromise<T> ifError(Consumer<Exception> errorCallback) {
		this.errorCallback = errorCallback;
		errorQueue.forEach(e -> errorCallback.accept(e));
		return this;
	}

	@NonNull
	public void resolve(T result) {
		if ( callback == null )
			resultQueue.add(result);
		else if ( !cancelled && !done )
			new Thread(() -> callback.accept(result)).start();
	}

	@NonNull
	public void error(Exception error) {
		if ( errorCallback == null )
			errorQueue.add(error);
		else if ( !cancelled && !done )
			new Thread(() -> errorCallback.accept(error)).start();
	}

	@Override
	public boolean cancel(boolean arg0) {
		cancelled = true;
		done = true;
		callback = null;
		errorCallback = null;
		errorQueue.add(new InterruptedException("Promise was cancelled"));
		return true;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		while ( resultQueue.size() <= 0 && errorQueue.size() <= 0 )
			Thread.sleep(FUTURE_CHECK_DELAY);
		if ( errorQueue.size() > 0  )
			throw new ExecutionException("Future ended with an error", errorQueue.remove(0));
		else
			return resultQueue.remove(0);
	}

	@Override
	public T get(long duration, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long start = System.currentTimeMillis();
		long end = TimeUnit.MILLISECONDS.convert(duration, unit);
		while ( resultQueue.size() <= 0 && errorQueue.size() <= 0 && start + System.currentTimeMillis() > end )
			Thread.sleep(FUTURE_CHECK_DELAY);
		if ( errorQueue.size() > 0  )
			throw new ExecutionException("Future ended with an error", errorQueue.remove(0));
		else if ( resultQueue.size() > 0  )
			return resultQueue.remove(0);
		else
			throw new TimeoutException("Future timed out");
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isDone() {
		return done;
	}
}
