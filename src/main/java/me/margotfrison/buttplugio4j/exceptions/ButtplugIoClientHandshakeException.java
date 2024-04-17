package me.margotfrison.buttplugio4j.exceptions;

public class ButtplugIoClientHandshakeException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientHandshakeException(String message) {
		super(message);
	}

	public ButtplugIoClientHandshakeException(String message, Throwable cause) {
		super(message, cause);
	}
}
