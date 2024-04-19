package me.margotfrison.buttplugio4j.client.simplified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.margotfrison.buttplugio4j.client.basic.BasicAsyncButtplugIoClient;
import me.margotfrison.buttplugio4j.client.basic.BasicButtplugIoListener;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientException;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientPromiseException;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;
import me.margotfrison.buttplugio4j.protocol.handshake.ServerInfo;

/**
 * A basic buttplog.io client with some of the basic stuff done for you.
 * It is totally loyal to the buttplug.io protocol.<br>
 * This client features :
 * <ul>Requests with {@link Promise}s callbacks. Failing if the timeout
 * is exceeded (as the buttplug.io server sometimes fail to send back
 * error messages)</ul>
 * TODO redo javadoc
 */
public class AsyncButtplugIoClient {
	/**
	 * This should be plenty enough (5 seconds) for a local server to respond.
	 * Use {@link AsyncButtplugIoClient#AsyncButtplugIoClient(String, long)} to
	 * specify the timeout if its not.
	 * @see AsyncButtplugIoClient#AsyncButtplugIoClient(String, long)
	 */
	public static final long DEFAULT_PROMISE_TIMEOUT_MS = 5000;

	private final BasicAsyncButtplugIoClient client;
	private final Thread timedPromiseFailer = new Thread(new TimedPromiseFailer());
	private boolean timedPromiseFailerStoped = false;
	private final Map<Message, TimedSimplePromise<Message>> unansweredSimplePromises = new HashMap<>();
	private final Lock unansweredPromisesLock = new ReentrantLock();
	@Getter
	private boolean stoped = true;
	@Getter
	private final List<AsyncButtplugIoListener> listeners = new ArrayList<>();
	@Getter
	private final long promisesTimeoutMs;

	/**
	 * Construct a {@link AsyncButtplugIoClient}.
	 * @param uri the URI of the buttplug.io server
	 * @param timeoutMs the timeout in milliseconds for every
	 * {@link Promise}s sent back after a request
	 */
	public AsyncButtplugIoClient(String uri, long timeoutMs) {
		this.client = new BasicAsyncButtplugIoClient(uri);
		this.client.addListener(new AsyncButtplugIoClientListener());
		this.promisesTimeoutMs = timeoutMs;
		this.timedPromiseFailer.start();
	}

	/**
	 * Construct a {@link AsyncButtplugIoClient} with a default
	 * timeout for all promises
	 * {@link AsyncButtplugIoClient#DEFAULT_PROMISE_TIMEOUT_MS}.
	 * @param uri the URI of the buttplug.io server
	 * @see {@link AsyncButtplugIoClient#DEFAULT_PROMISE_TIMEOUT_MS}
	 */
	public AsyncButtplugIoClient(String uri) {
		this(uri, DEFAULT_PROMISE_TIMEOUT_MS);
	}

	/**
	 * Add a {@link BasicButtplugIoListener} listener.
	 * @param listener a {@link BasicButtplugIoListener} listener
	 */
	public void addListener(AsyncButtplugIoListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a {@link BasicButtplugIoListener} listener.
	 * @param listener the {@link BasicButtplugIoListener} listener to remove
	 */
	public void removeListener(AsyncButtplugIoListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Listen the websocket server for responses.
	 */
	private class AsyncButtplugIoClientListener implements BasicButtplugIoListener {
		@Override
		public void onServerOpen() {
			// Search for a (or multiples) of RequestServerInfo and send them.
			// Because sendHandshake was waiting for the connection to be
			// established
			lockUnansweredPromises();
			try {
				for ( Entry<Message, TimedSimplePromise<Message>> promiseEntry : unansweredSimplePromises.entrySet() ) {
					if ( promiseEntry.getKey() instanceof RequestServerInfo )
						client.sendMessage(promiseEntry.getKey());
				}
			} finally {
				unansweredPromisesLock.unlock();
			}
		}

		@Override
		public void onMessages(Collection<Message> messages) {
			lockUnansweredPromises();
			try {
				for (Message message : messages) {
					// System messages
					if ( message.getId() == 0 ) {
						listeners.forEach(l -> l.onSubscribedMessage(message));
						continue;
					}
					// Simple promises
					for ( Entry<Message, TimedSimplePromise<Message>> promiseEntry : unansweredSimplePromises.entrySet() ) {
						if ( promiseEntry.getKey().getId() == message.getId() ) {
							System.out.println("Responded to " + promiseEntry.getKey());
							promiseEntry.getValue().resolve(message);
							unansweredSimplePromises.remove(promiseEntry.getKey());
							break;
						}
					}
				}
			} finally {
				unansweredPromisesLock.unlock();
			}
		}

		@Override
		public void onServerClose(Exception reason) {
			lockUnansweredPromises();
			try {
				stoped = true;
				timedPromiseFailerStoped = true;
				timedPromiseFailer.interrupt();
				unansweredSimplePromises.forEach((k, v) -> v.error(new ButtplugIoClientPromiseException("Server stopped before message with id %d recieved a response".formatted(k.getId()), reason)));
				listeners.forEach(l -> l.onServerClose(reason));
			} finally {
				unansweredPromisesLock.unlock();
			}
		}
		
		@Override
		public void onButtplugClientError(ButtplugIoClientException e) {
			lockUnansweredPromises();
			try {
				unansweredSimplePromises.forEach((k, v) -> v.error(e));
			} finally {
				unansweredPromisesLock.unlock();
			}
		}
	}

	private class TimedPromiseFailer implements Runnable {
		@Override
		public void run() {
			while ( !timedPromiseFailerStoped ) {
				lockUnansweredPromises();
				try {
					long killDelay = System.currentTimeMillis() - promisesTimeoutMs;
					for ( Entry<Message, TimedSimplePromise<Message>> unansweredPromise : unansweredSimplePromises.entrySet() ) {
						if ( unansweredPromise.getValue().getBornAt() > killDelay ) {
							System.out.println("Failed " + unansweredPromise.getKey());
							unansweredPromise.getValue().error(new ButtplugIoClientPromiseException("Server failed to send message back to the client in the allowed time for message with id %d".formatted(unansweredPromise.getKey().getId())));
							unansweredSimplePromises.remove(unansweredPromise.getKey());
						}
					}
				} finally {
					unansweredPromisesLock.unlock();
				}
				try {
					Thread.sleep(promisesTimeoutMs);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	/**
	 * Send one or more {@link Message}s to the buttplug.io server.
	 * @param <T> the assumed class of message received
	 * (not guaranteed).
	 * @param messages a list of {@link Message}s to send.
	 * Should not be null
	 * @return a list of {@link SimplePromise} that will be resolved as a
	 * {@link Message} server response, in the same order as the
	 * {@link Message} list given in argument.<br>
	 * It is guaranteed that the returned list will be
	 * non null and with the same size of the provided list.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> List<SimplePromise<T>> sendMessages(@NonNull List<Message> messages) {
		if ( messages.size() <= 0 )
			return new ArrayList<>();
		List<SimplePromise<T>> promises = new ArrayList<>();
		long bornAt = System.currentTimeMillis();
		lockUnansweredPromises();
		try {
			for ( Message message : messages ) {
				System.out.println("Sent " + message);
				TimedSimplePromise<T> promise = new TimedSimplePromise<T>(bornAt);
				promises.add(promise);
				unansweredSimplePromises.put(message, (TimedSimplePromise<Message>) promise);
			}
		} finally {
			unansweredPromisesLock.unlock();
		}
		client.sendMessages(messages);
		return promises;
	}

	/**
	 * Alias of {@link AsyncButtplugIoClient#sendMessages(List)} with varargs
	 * @see {@link AsyncButtplugIoClient#sendMessages(List)}
	 */
	public <T extends Message> List<SimplePromise<T>> sendMessages(@NonNull Message... messages) {
		return sendMessages(Arrays.asList(messages));
	}

	/**
	 * Send a single {@link Message} to the buttplug.io server.
	 * @param <T> the assumed class of message received
	 * (not guaranteed).
	 * @param message the {@link Message} to send.
	 * Should not be null
	 * @return a {@link SimplePromise} that will be resolved as the
	 * {@link Message} response from the server
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> SimplePromise<T> sendMessage(@NonNull Message message) {
		return (SimplePromise<T>) sendMessages(message).get(0);
	}

	/**
	 * Perform the handshake automatically with the provided
	 * {@link Message}. After the response is received through
	 * the {@link SimplePromise} you should be able to send any kind
	 * of messages to the server.
	 * @param message the {@link RequestServerInfo} to send.
	 * Should not be null
	 * @return a {@link SimplePromise} that will be resolved as the
	 * {@link ServerInfo} response from the server
	 */
	@SuppressWarnings("unchecked")
	public SimplePromise<ServerInfo> sendHandshake(@NonNull RequestServerInfo message) {
		// We try to connect and save the message to be sent to
		// the server after the connection while we prepare and
		// return the promise. For more details see
		// AsyncButtplugIoClientListener.onServerOpen()
		client.connect();
		TimedSimplePromise<?> promise = new TimedSimplePromise<ServerInfo>(System.currentTimeMillis());
		lockUnansweredPromises();
		try {
			unansweredSimplePromises.put(message, (TimedSimplePromise<Message>) promise);
		} finally {
			unansweredPromisesLock.unlock();
		}
		return (SimplePromise<ServerInfo>) promise;
	}

	/**
	 * Disconnect to buttplug.io web socket server. The listener
	 * {@link AsyncButtplugIoListener} will be noticed when
	 * the connection is successfully closed.
	 * @see AsyncButtplugIoListener#onServerClose()
	 */
	public void disconnect() {
		stoped = true;
		timedPromiseFailerStoped = true;
		timedPromiseFailer.interrupt();
		client.disconnect();
	}
	
	@SneakyThrows
	private void lockUnansweredPromises() {
		unansweredPromisesLock.lockInterruptibly();
	}
}
