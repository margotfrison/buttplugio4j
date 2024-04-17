package me.margotfrison.buttplugio4j.exceptions;

public class ButtplugIoClientInitException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientInitException(String message) {
		super(message);
	}

	public ButtplugIoClientInitException(String message, Throwable cause) {
		super(message, cause);
	}
}
