package me.margotfrison.buttplugio4j.exceptions;

public class ButtplugIoClientPromiseException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientPromiseException(String message) {
		super(message);
	}

	public ButtplugIoClientPromiseException(String message, Throwable cause) {
		super(message, cause);
	}
}
