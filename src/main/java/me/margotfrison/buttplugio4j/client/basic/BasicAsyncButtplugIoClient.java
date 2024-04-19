package me.margotfrison.buttplugio4j.client.basic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import lombok.Getter;
import lombok.NonNull;
import me.margotfrison.buttplugio4j.client.simplified.AsyncButtplugIoClient;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientException;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientInitException;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientJsonException;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.MessageJsonParser;

/**
 * The most basic buttplog.io client I could think of. It is totally loyal to the
 * buttplug.io protocol.<br>
 * <b>Be aware !</b> In this client, you have to manually handshake and open
 * the web socket connection with {@link BasicAsyncButtplugIoClient#connect()}.<br>
 * <b>Also</b>, only the <a href="https://buttplug-spec.docs.buttplug.io/docs/spec">
 * protocol version 3</a> is currently supported.<br>
 * <b>Furthermore</b> it's up to you to implement a way to keep track of the message
 * ids sent and received.
 * @see AsyncButtplugIoClient
 * @see SyncButtplugIoClient
 */
public class BasicAsyncButtplugIoClient {
	private final WebSocketClient wsClient;
	@Getter
	private boolean stoped = true;
	@Getter
	private final List<BasicButtplugIoListener> listeners = new ArrayList<>();

	/**
	 * Construct a {@link BasicAsyncButtplugIoClient}.
	 * @param uri the URI of the buttplug.io server
	 */
	public BasicAsyncButtplugIoClient(String uri) {
		try {
			this.wsClient = new WebSocketClientWrapper(new URI(uri), new BasicAsyncButtplugIoClientListener());
		} catch (URISyntaxException e) {
			throw new ButtplugIoClientInitException("Cannot parse URI : %s".formatted(uri), e);
		}
	}

	/**
	 * Add a {@link BasicButtplugIoListener} listener.
	 * @param listener a {@link BasicButtplugIoListener} listener
	 */
	public void addListener(BasicButtplugIoListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a {@link BasicButtplugIoListener} listener.
	 * @param listener the {@link BasicButtplugIoListener} listener to remove
	 */
	public void removeListener(BasicButtplugIoListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Listen the websocket server for responses.
	 */
	private class BasicAsyncButtplugIoClientListener implements WebSocketClientListener {
		@Override
		public void onOpen(ServerHandshake handshakedata) {
			stoped = false;
			listeners.forEach(l -> l.onServerOpen());
		}

		@Override
		public void onMessage(String json) {
			// When a JSON is received : parse, and for every messages : complete future
			try {
				Collection<Message> messages = MessageJsonParser.fromJson(json);
				listeners.forEach(l -> l.onMessages(messages));
			} catch (Exception e) {
				listeners.forEach(l -> l.onButtplugClientError(new ButtplugIoClientJsonException("Unable to parse the JSON received from the server : %s".formatted(json), e)));
			}
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			stoped = true;
			listeners.forEach(l -> l.onServerClose(new ButtplugIoClientException("Server closed because : " + reason)));
		}

		@Override
		public void onError(Exception ex) {
			listeners.forEach(l -> l.onWebSocketError());
		}
	}

	/**
	 * Send one or more {@link Message}s to the buttplug.io server.
	 * @param <T> the assumed class of message received
	 * (not guaranteed).
	 * @param messages a list of {@link Message}s to send.
	 * Should not be null
	 * @return a list of {@link Future} that will be resolved as a
	 * {@link Message} server response, in the same order as the
	 * {@link Message} list given in argument.<br>
	 * It is guaranteed that the returned list will be
	 * non null and with the same size of the provided list.
	 */
	public void sendMessages(@NonNull List<Message> messages) {
		wsClient.send(MessageJsonParser.toJson(messages));
	}

	/**
	 * Alias of {@link BasicAsyncButtplugIoClient#sendMessages(List)} with varargs
	 * @see {@link BasicAsyncButtplugIoClient#sendMessages(List)}
	 */
	public void sendMessages(@NonNull Message... messages) {
		sendMessages(Arrays.asList(messages));
	}

	/**
	 * Send a single {@link Message} to the buttplug.io server.
	 * @param <T> the assumed class of message received
	 * (not guaranteed).
	 * @param message the {@link Message} to send.
	 * Should not be null
	 * @return a {@link Future} that will be resolved as the
	 * {@link Message} response from the server
	 */
	public void sendMessage(@NonNull Message message) {
		sendMessages(message);
	}

	/**
	 * Connect to buttplug.io web socket server. The listener
	 * {@link BasicButtplugIoListener} will be noticed when
	 * the connection is successfully established.
	 * @see BasicButtplugIoListener#onServerOpen()
	 */
	public void connect() {
		wsClient.connect();
	}

	/**
	 * Disconnect to buttplug.io web socket server. The listener
	 * {@link BasicButtplugIoListener} will be noticed when
	 * the connection is successfully closed.
	 * @see BasicButtplugIoListener#onServerClose()
	 */
	public void disconnect() {
		stoped = true;
		wsClient.close();
	}
}
