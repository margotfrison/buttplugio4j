package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import java.util.List;

import lombok.SneakyThrows;
import me.margotfrison.buttplugio4j.client.simplified.AsyncButtplugIoClient;
import me.margotfrison.buttplugio4j.client.simplified.Promise;
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
 * This example shows how to use the {@link AsyncButtplugIoClient} class
 * to send and receive messages asynchronously using {@link Promise}s.
 */
class AsyncButtplugIoClientExempleWithPromises {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";

	@SuppressWarnings("unused")
	static void doExample() {
		// Init client and do handshake
		AsyncButtplugIoClient client = new AsyncButtplugIoClient(BUTTPLUG_IO_URL);
		client.sendHandshake(new RequestServerInfo(CLIENT_NAME, Message.LAST_SUPPORTED_VERSION)).then((ignored) -> {
			// Send RequestDeviceList
			client.sendSimpleMessage(new RequestDeviceList()).then((message) -> {
				// Send a ScalarCommandRequest to every devices connected
				DeviceList deviceList = (DeviceList) message;
				for (int i = 0; i < deviceList.getDevices().size(); i++) {
					Device device = deviceList.getDevices().get(i);
					for (int j = 0; j < device.getDeviceMessages().getScalarCmd().size(); j++) {
						ScalarCommand cmd = device.getDeviceMessages().getScalarCmd().get(j);
						client.sendSimpleMessage(new ScalarCommandRequest(device.getDeviceIndex(), List.of(new Scalar(j, 0.5, cmd.getActuatorType()))));
					}
				}
				// Wait 1 second and send a StopAllDevices command
				waitFor(1000);
				client.sendSimpleMessage(new StopAllDevices()).then((ignored2) -> {
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

	@SneakyThrows
	private static void waitFor(long millis) {
		Thread.sleep(millis);
	}
}
