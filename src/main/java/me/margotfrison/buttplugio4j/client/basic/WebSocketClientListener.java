package me.margotfrison.buttplugio4j.client.basic;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Contains all the abstract methods of {@link WebSocketClient}.
 * @see {@link WebSocketClientWrapper}
 */
interface WebSocketClientListener {
	default void onOpen(ServerHandshake handshakedata) { }
	default void onMessage(String message) { }
	default void onClose(int code, String reason, boolean remote) { }
	default void onError(Exception ex) { }
}
