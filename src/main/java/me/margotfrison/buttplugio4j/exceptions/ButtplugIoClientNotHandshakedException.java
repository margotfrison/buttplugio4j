package me.margotfrison.buttplugio4j.exceptions;

public class ButtplugIoClientNotHandshakedException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientNotHandshakedException(String message) {
		super(message);
	}

	public ButtplugIoClientNotHandshakedException(String message, Throwable cause) {
		super(message, cause);
	}
}
