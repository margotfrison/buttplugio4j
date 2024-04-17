package me.margotfrison.buttplugio4j.exceptions;

public class ButtplugIoClientJsonException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientJsonException(String message) {
		super(message);
	}

	public ButtplugIoClientJsonException(String message, Throwable cause) {
		super(message, cause);
	}
}
