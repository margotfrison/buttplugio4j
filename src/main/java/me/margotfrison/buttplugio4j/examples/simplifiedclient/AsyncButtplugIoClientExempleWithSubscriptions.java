package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;
import me.margotfrison.buttplugio4j.client.simplified.AsyncButtplugIoClient;
import me.margotfrison.buttplugio4j.client.simplified.AsyncButtplugIoListener;
import me.margotfrison.buttplugio4j.client.simplified.Promise;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceAdded;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceRemoved;
import me.margotfrison.buttplugio4j.protocol.enumeration.StartScanning;
import me.margotfrison.buttplugio4j.protocol.enumeration.StopScanning;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;

/**
 * This example shows how to use the {@link AsyncButtplugIoClient} class
 * to send and receive messages asynchronously using {@link Promise}s.
 */
@Log
class AsyncButtplugIoClientExempleWithSubscriptions implements AsyncButtplugIoListener {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";
	private static List<String> devicesAdded = new ArrayList<>();
	private static List<Integer> devicesRemoved = new ArrayList<>();

	void doExample() {
		// Init client and do handshake
		AsyncButtplugIoClient client = new AsyncButtplugIoClient(BUTTPLUG_IO_URL);
		client.addListener(this);
		client.sendHandshake(new RequestServerInfo(CLIENT_NAME, Message.LAST_SUPPORTED_VERSION)).then((ignored) -> {
			// Send StartScanning
			client.sendMessage(new StartScanning()).then((ignored2) -> {
				// Wait for 10 seconds
				ExempleUtils.waitFor(10000);
				client.sendMessage(new StopScanning()).then((ignored3) -> {
					// Log all device scanned
					log.info("List of devices (names) found during the scan : " + devicesAdded.toString());
					log.info("List of devices (ids) disconnected during the scan : " + devicesRemoved.toString());
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
	
	@Override
	public void onSubscribedMessage(Message message) {
		if ( message instanceof DeviceAdded ) {
			// Add all scanned device to the list for log
			devicesAdded.add(((DeviceAdded) message).getDeviceName());
		} else if ( message instanceof DeviceRemoved ) {
			// Add all scanned device to the list for log
			devicesRemoved.add(((DeviceRemoved) message).getDeviceIndex());
		}
	}
}
