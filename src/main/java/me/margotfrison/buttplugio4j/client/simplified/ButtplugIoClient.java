package me.margotfrison.buttplugio4j.client.simplified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import me.margotfrison.buttplugio4j.client.basic.BasicButtplugIoClient;
import me.margotfrison.buttplugio4j.client.basic.BasicButtplugIoListener;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientException;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientPromiseException;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.StartScanning;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;
import me.margotfrison.buttplugio4j.protocol.handshake.ServerInfo;

/**
 * A buttplog.io client with some of the basic stuff done for you.
 * It is totally loyal to the buttplug.io protocol.<br>
 * This client features :
 * <ul>Request fails if the timeout is exceeded (as the buttplug.io
 * server sometimes fail to send back error messages). The timeout
 * is configurable with {@link ButtplugIoClient#ButtplugIoClient(String, long)}
 * and is set by default with {@link ButtplugIoClient#DEFAULT_PROMISE_TIMEOUT_MS}.</ul>
 * <ul>Asynchronous requests with {@link Promise}s callbacks.</ul>
 * <ul>Asynchronous requests with {@link Future}s ({@link Promise} implements
 * {@link Future}).</ul>
 * <ul>Asynchronous response to subscription requests (ex {@link StartScanning})
 * via the {@link ButtplugIoListener}.</ul>
 * <ul>Synchronous requests (including subscription requests).</ul>
 */
public class ButtplugIoClient {
	/**
	 * This should be plenty enough (5 seconds) for a local server to respond.
	 * Use {@link ButtplugIoClient#AsyncButtplugIoClient(String, long)} to
	 * specify the timeout if its not.
	 * @see ButtplugIoClient#ButtplugIoClient(String, long)
	 */
	public static final long DEFAULT_PROMISE_TIMEOUT_MS = 5000;

	private final BasicButtplugIoClient client;
	private final Thread timedPromiseFailer = new Thread(new TimedPromiseFailer());
	private boolean timedPromiseFailerStoped = false;
	private final Map<Message, TimedSimplePromise<Message>> unansweredSimplePromises = new HashMap<>();
	private final Lock unansweredPromisesLock = new ReentrantLock();
	@Getter
	private final List<ButtplugIoListener> listeners = new ArrayList<>();
	@Getter @Setter
	private long promisesTimeoutMs;

	/**
	 * Construct a {@link ButtplugIoClient} with a specified timeout
	 * for the {@link Promise}s.
	 * @param uri the URI of the buttplug.io server
	 * @param timeoutMs the timeout in milliseconds for every
	 * {@link Promise}s sent back after a request
	 * @see ButtplugIoClient#ButtplugIoClient(String)
	 */
	public ButtplugIoClient(String uri, long timeoutMs) {
		this.client = new BasicButtplugIoClient(uri);
		this.client.addListener(new AsyncButtplugIoClientListener());
		this.promisesTimeoutMs = timeoutMs;
		this.timedPromiseFailer.start();
	}

	/**
	 * Construct a {@link ButtplugIoClient} with a default
	 * timeout for all {@link Promise}s ({@link ButtplugIoClient#DEFAULT_PROMISE_TIMEOUT_MS}).
	 * @param uri the URI of the buttplug.io server
	 * @see {@link ButtplugIoClient#DEFAULT_PROMISE_TIMEOUT_MS}
	 * @see ButtplugIoClient#ButtplugIoClient(String, long)
	 */
	public ButtplugIoClient(String uri) {
		this(uri, DEFAULT_PROMISE_TIMEOUT_MS);
	}

	/**
	 * Add a {@link ButtplugIoListener} listener.
	 * @param listener a {@link ButtplugIoListener} listener
	 */
	public void addListener(ButtplugIoListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a {@link ButtplugIoListener} listener.
	 * @param listener the {@link ButtplugIoListener} listener to remove
	 */
	public void removeListener(ButtplugIoListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Listen the {@link BasicButtplugIoListener} super client for responses.
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

	/**
	 * A {@link Runnable} intended to be run in a {@link Thread} to cancel
	 * the {@link Promise}s once they reached their timeout
	 */
	private class TimedPromiseFailer implements Runnable {
		@Override
		public void run() {
			while ( !timedPromiseFailerStoped ) {
				lockUnansweredPromises();
				try {
					long killDelay = System.currentTimeMillis() - promisesTimeoutMs;
					for ( Entry<Message, TimedSimplePromise<Message>> unansweredPromise : unansweredSimplePromises.entrySet() ) {
						if ( unansweredPromise.getValue().getBornAt() > killDelay ) {
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
	 * Send one or more {@link Message}s to the buttplug.io server
	 * asynchronously.
	 * @param <T> the assumed class of the received {@link Message}s
	 * (not guaranteed)
	 * @param messages a list of {@link Message}s to send.
	 * Should not be null
	 * @return a list of {@link SimplePromise} that will be resolved as a
	 * {@link Message} server response, in the same order as the
	 * {@link Message} list given in argument.<br>
	 * It is guaranteed that the returned list will be
	 * non null and with the same size of the provided list.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> List<SimplePromise<T>> sendMessagesAsync(@NonNull List<Message> messages) {
		if ( messages.size() <= 0 )
			return new ArrayList<>();
		List<SimplePromise<T>> promises = new ArrayList<>();
		long bornAt = System.currentTimeMillis();
		lockUnansweredPromises();
		try {
			for ( Message message : messages ) {
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
	 * Alias of {@link ButtplugIoClient#sendMessagesAsync(List)} with varargs
	 * @see {@link ButtplugIoClient#sendMessagesAsync(List)}
	 */
	public <T extends Message> List<SimplePromise<T>> sendMessagesAsync(@NonNull Message... messages) {
		return sendMessagesAsync(Arrays.asList(messages));
	}

	/**
	 * Send a single {@link Message} to the buttplug.io server
	 * asynchronously.
	 * @param <T> the assumed class of the received message
	 * (not guaranteed)
	 * @param message the {@link Message} to send.
	 * Should not be null
	 * @return a {@link SimplePromise} that will be resolved as the
	 * {@link Message} response from the server
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> SimplePromise<T> sendMessageAsync(@NonNull Message message) {
		return (SimplePromise<T>) sendMessagesAsync(message).get(0);
	}

	/**
	 * Perform the handshake asynchronously with the provided
	 * {@link RequestServerInfo} (also connect the web server).
	 * After the response has been received through the
	 * {@link SimplePromise} you should be able to send any
	 * kind of messages to the server.
	 * @param message the {@link RequestServerInfo} to send.
	 * Should not be null
	 * @return a {@link SimplePromise} that will be resolved as the
	 * {@link ServerInfo} response from the server
	 */
	@SuppressWarnings("unchecked")
	public SimplePromise<ServerInfo> sendHandshakeAsync(@NonNull RequestServerInfo message) {
		// We try to connect and save the message to be sent to
		// the server after the connection while we prepare and
		// return the promise. For more details see
		// ButtplugIoClientListener.onServerOpen()
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
	 * Send one or more {@link Message}s to the buttplug.io server
	 * synchronously.
	 * @param <T> the assumed class of message received
	 * (not guaranteed)
	 * @param messages a list of {@link Message}s to send.
	 * Should not be null
	 * @return a list of {@link Message} responded by the server,
	 * in the same order as the {@link Message} list given in
	 * argument.<br>
	 * It is guaranteed that the returned list will be
	 * non null and with the same size of the provided list.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> List<T> sendMessagesSync(@NonNull List<Message> messages) {
		return (List<T>) sendMessagesAsync(messages)
				.stream()
				.map(p -> getPromise(p))
				.toList();
	}

	/**
	 * Alias of {@link SyncButtplugIoClient#sendMessages(List)} with varargs
	 * @see {@link SyncButtplugIoClient#sendMessages(List)}
	 */
	public <T extends Message> List<T> sendMessagesSync(@NonNull Message... messages) {
		return sendMessagesSync(Arrays.asList(messages));
	}

	/**
	 * Send a single {@link Message} to the buttplug.io server
	 * synchronously.
	 * @param <T> the assumed class of message received
	 * (not guaranteed)
	 * @param message the {@link Message} to send.
	 * Should not be null
	 * @return a {@link Message} responded by the server
	 */
	public <T extends Message> T sendMessageSync(@NonNull Message message) {
		return getPromise(sendMessageAsync(message));
	}

	/**
	 * Perform the handshake synchronously with the provided
	 * {@link RequestServerInfo}. After the response is received
	 * you should be able to send any kind of messages to the
	 * server.
	 * @param message the {@link RequestServerInfo} to send.
	 * Should not be null
	 * @return a {@link ServerInfo} response from the server
	 */
	public ServerInfo sendHandshakeSync(@NonNull RequestServerInfo message) {
		return getPromise(sendHandshakeAsync(message));
	}

	/**
	 * Disconnect to buttplug.io web socket server. The listener
	 * {@link ButtplugIoListener} will be noticed when
	 * the connection is successfully closed.
	 * @see ButtplugIoListener#onServerClose()
	 */
	public void disconnect() {
		timedPromiseFailerStoped = true;
		timedPromiseFailer.interrupt();
		client.disconnect();
	}

	public boolean isStoped() {
		return client.isStoped();
	}

	/**
	 * {@link SneakyThrows} for {@link Lock#lockInterruptibly()}
	 */
	@SneakyThrows
	private void lockUnansweredPromises() {
		unansweredPromisesLock.lockInterruptibly();
	}


	/**
	 * {@link SneakyThrows} for {@link Future#get()} implemented
	 * by {@link Promise}
	 */
	@SneakyThrows
	private <T extends Object> T getPromise(Promise<T> promise) {
		return promise.get();
	}
}
