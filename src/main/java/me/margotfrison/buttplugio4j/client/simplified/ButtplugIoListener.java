package me.margotfrison.buttplugio4j.client.simplified;

import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.StartScanning;

/**
 * A listener for the client {@link ButtplugIoClient}.
 * It includes subscription messages (ex {@link StartScanning})
 */
public interface ButtplugIoListener {
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
	 * Called when the server sent a response to a subscription command
	 * (i.e. StartScanning => DeviceAdded)
	 */
	default void onSubscribedMessage(Message message) { }
}
