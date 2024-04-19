package me.margotfrison.buttplugio4j.client.simplified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

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
 */
public class SyncButtplugIoClient {
	private final BasicAsyncButtplugIoClient client;
	private final Map<Message, SimplePromise<Message>> unansweredSimplePromises = new HashMap<>();
	@Getter
	private boolean stoped = true;
	@Getter
	private final List<ButtplugIoListener> listeners = new ArrayList<>();

	/**
	 * Construct a {@link SyncButtplugIoClient}.
	 * @param uri the URI of the buttplug.io server
	 * {@link Promise}s sent back after a request
	 */
	public SyncButtplugIoClient(String uri) {
		this.client = new BasicAsyncButtplugIoClient(uri);
		this.client.addListener(new AsyncButtplugIoClientListener());
	}

	/**
	 * Add a {@link BasicButtplugIoListener} listener.
	 * @param listener a {@link BasicButtplugIoListener} listener
	 */
	public void addListener(ButtplugIoListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a {@link BasicButtplugIoListener} listener.
	 * @param listener the {@link BasicButtplugIoListener} listener to remove
	 */
	public void removeListener(ButtplugIoListener listener) {
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
			for ( Entry<Message, SimplePromise<Message>> promiseEntry : unansweredSimplePromises.entrySet() ) {
				if ( promiseEntry.getKey() instanceof RequestServerInfo )
					client.sendMessage(promiseEntry.getKey());
			}
		}

		@Override
		public void onMessages(Collection<Message> messages) {
			for (Message message : messages) {
				// System messages
				if ( message.getId() == 0 ) {
					listeners.forEach(l -> l.onSubscribedMessage(message));
					continue;
				}
				// Simple promises
				for ( Entry<Message, SimplePromise<Message>> promiseEntry : unansweredSimplePromises.entrySet() ) {
					if ( promiseEntry.getKey().getId() == message.getId() ) {
						System.out.println("Responded to " + promiseEntry.getKey());
						promiseEntry.getValue().resolve(message);
						unansweredSimplePromises.remove(promiseEntry.getKey());
						break;
					}
				}
			}
		}

		@Override
		public void onServerClose(Exception reason) {
			stoped = true;
			unansweredSimplePromises.forEach((k, v) -> v.error(new ButtplugIoClientPromiseException("Server stopped before message with id %d recieved a response".formatted(k.getId()), reason)));
			listeners.forEach(l -> l.onServerClose(reason));
		}
		
		@Override
		public void onButtplugClientError(ButtplugIoClientException e) {
			unansweredSimplePromises.forEach((k, v) -> v.error(e));
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
	public <T extends Message> List<T> sendMessages(@NonNull List<Message> messages) {
		if ( messages.size() <= 0 )
			return new ArrayList<>();
		List<SimplePromise<T>> promises = new ArrayList<>();
		for ( Message message : messages ) {
			SimplePromise<T> promise = new SimplePromise<T>();
			unansweredSimplePromises.put(message, (SimplePromise<Message>) promise);
			promises.add(promise);
		}
		client.sendMessages(messages);
		return promises.stream()
				.map(p -> getPromise(p))
				.toList();
	}

	/**
	 * Alias of {@link SyncButtplugIoClient#sendMessages(List)} with varargs
	 * @see {@link SyncButtplugIoClient#sendMessages(List)}
	 */
	public <T extends Message> List<T> sendMessages(@NonNull Message... messages) {
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
	public <T extends Message> T sendMessage(@NonNull Message message) {
		return (T) sendMessages(message).get(0);
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
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public ServerInfo sendHandshake(@NonNull RequestServerInfo message) {
		// We try to connect and save the message to be sent to
		// the server after the connection while we prepare and
		// return the promise. For more details see
		// AsyncButtplugIoClientListener.onServerOpen()
		client.connect();
		SimplePromise<?> promise = new SimplePromise<ServerInfo>();
		unansweredSimplePromises.put(message, (SimplePromise<Message>) promise);
		return (ServerInfo) promise.get();
	}

	/**
	 * Disconnect to buttplug.io web socket server. The listener
	 * {@link ButtplugIoListener} will be noticed when
	 * the connection is successfully closed.
	 * @see ButtplugIoListener#onServerClose()
	 */
	public void disconnect() {
		stoped = true;
		client.disconnect();
	}

	@SneakyThrows
	private <T extends Object> T getPromise(Promise<T> promise) {
		return promise.get();
	}
}
