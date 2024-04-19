package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import java.util.List;

import me.margotfrison.buttplugio4j.client.simplified.SyncButtplugIoClient;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.Device;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.RequestDeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.ScalarCommand;
import me.margotfrison.buttplugio4j.protocol.genericdevice.Scalar;
import me.margotfrison.buttplugio4j.protocol.genericdevice.ScalarCommandRequest;
import me.margotfrison.buttplugio4j.protocol.genericdevice.StopAllDevices;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;

/**
 * This example shows how to use the {@link SyncButtplugIoClient} class
 * to send and receive messages synchronously.
 */
class SyncButtplugIoClientExemple {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";

	void doExample() {
		// Init client and do handshake
		SyncButtplugIoClient client = new SyncButtplugIoClient(BUTTPLUG_IO_URL);
		client.sendHandshake(new RequestServerInfo(CLIENT_NAME, Message.LAST_SUPPORTED_VERSION));
		// Send RequestDeviceList
		DeviceList deviceList = client.sendMessage(new RequestDeviceList());
		for (int i = 0; i < deviceList.getDevices().size(); i++) {
			Device device = deviceList.getDevices().get(i);
			for (int j = 0; j < device.getDeviceMessages().getScalarCmd().size(); j++) {
				ScalarCommand cmd = device.getDeviceMessages().getScalarCmd().get(j);
				client.sendMessage(new ScalarCommandRequest(device.getDeviceIndex(), List.of(new Scalar(j, 0.5, cmd.getActuatorType()))));
			}
		}
		// Wait 1 second and send a StopAllDevices command
		ExempleUtils.waitFor(1000);
		client.sendMessage(new StopAllDevices());
		// Then disconnect
		client.disconnect();
	}
}
