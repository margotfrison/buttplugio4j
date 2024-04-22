package me.margotfrison.buttplugio4j.examples;

import java.util.List;

import me.margotfrison.buttplugio4j.client.simplified.ButtplugIoClient;
import me.margotfrison.buttplugio4j.client.simplified.Promise;
import me.margotfrison.buttplugio4j.protocol.enumeration.Device;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.RequestDeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.ScalarCommand;
import me.margotfrison.buttplugio4j.protocol.genericdevice.Scalar;
import me.margotfrison.buttplugio4j.protocol.genericdevice.ScalarCommandRequest;
import me.margotfrison.buttplugio4j.protocol.genericdevice.StopAllDevices;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;

/**
 * This example shows how to use the {@link ButtplugIoClient} class
 * to send and receive messages asynchronously using {@link Promise}s.
 */
class AsyncButtplugIoClientExempleWithPromises {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";

	void doExample() {
		// Init client and do handshake
		ButtplugIoClient client = new ButtplugIoClient(BUTTPLUG_IO_URL);
		client.sendHandshakeAsync(new RequestServerInfo(CLIENT_NAME)).then((ignored) -> {
			// Send RequestDeviceList
			client.sendMessageAsync(new RequestDeviceList()).then((message) -> {
				// Send a ScalarCommandRequest to every devices connected
				DeviceList deviceList = (DeviceList) message;
				for (int i = 0; i < deviceList.getDevices().size(); i++) {
					Device device = deviceList.getDevices().get(i);
					for (int j = 0; j < device.getDeviceMessages().getScalarCmd().size(); j++) {
						ScalarCommand cmd = device.getDeviceMessages().getScalarCmd().get(j);
						client.sendMessageAsync(new ScalarCommandRequest(device.getDeviceIndex(), List.of(new Scalar(j, 0.5, cmd.getActuatorType()))));
					}
				}
				// Wait 1 second and send a StopAllDevices command
				ExempleUtils.waitFor(1000);
				client.sendMessageAsync(new StopAllDevices()).then((ignored2) -> {
					// Then disconnect
					client.disconnect();
				}).ifError((e) -> {
					e.printStackTrace();
					client.disconnect();
				});
			}).ifError((e) -> {
				e.printStackTrace();
				client.disconnect();
			});
		}).ifError((e) -> {
			e.printStackTrace();
			client.disconnect();
		});
	}
}
