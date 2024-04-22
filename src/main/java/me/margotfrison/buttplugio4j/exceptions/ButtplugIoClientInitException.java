package me.margotfrison.buttplugio4j.exceptions;

/**
 * Happen when the basic setup of a client failed (no connection attempted yet)
 */
public class ButtplugIoClientInitException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientInitException(String message) {
		super(message);
	}

	public ButtplugIoClientInitException(String message, Throwable cause) {
		super(message, cause);
	}
}
