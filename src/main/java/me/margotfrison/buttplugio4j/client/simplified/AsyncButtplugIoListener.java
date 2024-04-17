package me.margotfrison.buttplugio4j.client.simplified;

public interface AsyncButtplugIoListener {
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
}
