package me.margotfrison.buttplugio4j.client.basic;

interface ButtplugIoListener {
	default void onServerClose() { }
	default void onWebSocketError() { }
}
