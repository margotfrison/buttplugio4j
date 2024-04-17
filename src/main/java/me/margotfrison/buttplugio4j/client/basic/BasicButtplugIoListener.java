package me.margotfrison.buttplugio4j.client.basic;

import java.util.Collection;

import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientException;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;

public interface BasicButtplugIoListener {
	/**
	 * Called when the web socket client is connected to the
	 * buttplug.io server.<br>
	 * <b>Notes</b> : you must perform an handshake
	 * ({@link RequestServerInfo}) before sending any messages.
	 */
	default void onServerOpen() { }

	/**
	 * Called when the server close the connection whether
	 * the server abruptly closed the connection or the
	 * client requested it
	 * 
	 */
	default void onServerClose(Exception reason) { }

	/**
	 * Called when the web socket client encounter an error
	 */
	default void onWebSocketError() { }

	/**
	 * Called when the buttplug.io client encounter an error when receiving a message
	 */
	default void onButtplugClientError(ButtplugIoClientException e) { }

	/**
	 * Called when the server sent back a list of {@link Message}s
	 * @param messages
	 */
	default void onMessages(Collection<Message> messages) { }
}
