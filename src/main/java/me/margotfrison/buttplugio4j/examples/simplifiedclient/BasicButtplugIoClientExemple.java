package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import java.util.Collection;
import java.util.List;

import me.margotfrison.buttplugio4j.client.basic.BasicAsyncButtplugIoClient;
import me.margotfrison.buttplugio4j.client.basic.BasicButtplugIoListener;
import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientException;
import me.margotfrison.buttplugio4j.protocol.Message;
import me.margotfrison.buttplugio4j.protocol.enumeration.Device;
import me.margotfrison.buttplugio4j.protocol.enumeration.DeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.RequestDeviceList;
import me.margotfrison.buttplugio4j.protocol.enumeration.ScalarCommand;
import me.margotfrison.buttplugio4j.protocol.genericdevice.Scalar;
import me.margotfrison.buttplugio4j.protocol.genericdevice.ScalarCommandRequest;
import me.margotfrison.buttplugio4j.protocol.genericdevice.StopAllDevices;
import me.margotfrison.buttplugio4j.protocol.handshake.RequestServerInfo;
import me.margotfrison.buttplugio4j.protocol.handshake.ServerInfo;

/**
 * This example shows how to use the {@link BasicAsyncButtplugIoClient} class
 * to send and receive messages asynchronously with listeners
 */
class BasicButtplugIoClientExemple implements BasicButtplugIoListener {
	private static final String BUTTPLUG_IO_URL = "ws://localhost:12345";
	private static final String CLIENT_NAME = "buttplugio4j";
	
	private BasicAsyncButtplugIoClient client;

	void doExample() {
		// Init client and do handshake
		client = new BasicAsyncButtplugIoClient(BUTTPLUG_IO_URL);
		client.addListener(this);
		client.connect();
	}
	
	@Override
	public void onServerOpen() {
		// Server is opened
		// Ask for SeverInfo (handshake)
		client.sendMessage(new RequestServerInfo(CLIENT_NAME, Message.LAST_SUPPORTED_VERSION));
	}
	
	@Override
	public void onMessages(Collection<Message> messages) {
		System.out.println(messages);
		messages.forEach(message -> {
			System.out.println(message);
			if ( message instanceof ServerInfo ) {
				// We received the response for RequestServerInfo
				// Ask for connected devices
				client.sendMessage(new RequestDeviceList());
			} else if ( message instanceof DeviceList deviceList ) {
				// We received the response for RequestDeviceList
				// Ask to vibrate (scalar command) every toy
				for (int i = 0; i < deviceList.getDevices().size(); i++) {
					Device device = deviceList.getDevices().get(i);
					for (int j = 0; j < device.getDeviceMessages().getScalarCmd().size(); j++) {
						ScalarCommand cmd = device.getDeviceMessages().getScalarCmd().get(j);
						client.sendMessage(new ScalarCommandRequest(device.getDeviceIndex(), List.of(new Scalar(j, 0.5, cmd.getActuatorType()))));
					}
				}
				// Wait a moment
				ExempleUtils.waitFor(1000);
				// Stop and close connection
				client.sendMessage(new StopAllDevices());
				client.disconnect();
			}
		});
	}

	@Override
	public void onButtplugClientError(ButtplugIoClientException e) {
		e.printStackTrace();
		client.disconnect();
	}

	@Override
	public void onServerClose(Exception e) {
		e.printStackTrace();
		client.disconnect();
	}
}
