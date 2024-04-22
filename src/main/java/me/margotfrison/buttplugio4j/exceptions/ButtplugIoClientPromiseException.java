package me.margotfrison.buttplugio4j.exceptions;

/**
 * Happen when a promise times out or canceled due to connection shutdown
 */
public class ButtplugIoClientPromiseException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientPromiseException(String message) {
		super(message);
	}

	public ButtplugIoClientPromiseException(String message, Throwable cause) {
		super(message, cause);
	}
}
