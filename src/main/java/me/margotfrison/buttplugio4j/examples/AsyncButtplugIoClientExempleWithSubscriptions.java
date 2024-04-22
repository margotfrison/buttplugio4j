package me.margotfrison.buttplugio4j.examples;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;
import me.margotfrison.buttplugio4j.client.simplified.ButtplugIoClient;
import me.margotfrison.buttplugio4j.client.simplified.ButtplugIoListener;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceAdded;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceRemoved;
import me.margotfrison.buttplugio4j.protocol.enumeration.StartScanning;
import me.margotfrison.buttplugio4j.protocol.enumeration.StopScanning;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;

/**
 * This example shows how to use the {@link ButtplugIoClient} class
 * to send and receive subscription messages (ex scanning command).
 */
@Log
class AsyncButtplugIoClientExempleWithSubscriptions implements ButtplugIoListener {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";
	private static List<String> devicesAdded = new ArrayList<>();
	private static List<Integer> devicesRemoved = new ArrayList<>();

	void doExample() {
		// Init client and do handshake
		ButtplugIoClient client = new ButtplugIoClient(BUTTPLUG_IO_URL);
		client.addListener(this);
		client.sendHandshakeAsync(new RequestServerInfo(CLIENT_NAME)).then((ignored) -> {
			// Send StartScanning
			client.sendMessageAsync(new StartScanning()).then((ignored2) -> {
				// Wait for 10 seconds
				ExempleUtils.waitFor(5000);
				client.sendMessageAsync(new StopScanning()).then((ignored3) -> {
					// Log all device scanned
					log.info("List of devices (names) found during the scan : " + devicesAdded.toString());
					log.info("List of devices (ids) disconnected during the scan : " + devicesRemoved.toString());
					client.disconnectAsync();
				}).ifError((e) -> {
					e.printStackTrace();
					client.disconnectAsync();
				});
			}).ifError((e) -> {
				e.printStackTrace();
				client.disconnectAsync();
			});
		}).ifError((e) -> {
			e.printStackTrace();
			client.disconnectAsync();
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
