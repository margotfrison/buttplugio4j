package me.margotfrison.buttplugio4j.client.basic;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Transform the {@link WebSocketClient} abstract class into this wrapper
 * and a listener setted by the constructor.<br>
 * This class is not intended to be used by the final user of this library. Use
 * the much simpler {@link SimpleAsyncButtplugIoClient} instead.
 */
class WebSocketClientWrapper extends WebSocketClient {
	private final WebSocketClientListener listener;

	public WebSocketClientWrapper(URI uri, WebSocketClientListener listener) {
		super(uri);
		this.listener = listener;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		listener.onOpen(handshakedata);
	}

	@Override
	public void onMessage(String message) {
		listener.onMessage(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		listener.onClose(code, reason, remote);
	}

	@Override
	public void onError(Exception ex) {
		listener.onError(ex);
	}
}
