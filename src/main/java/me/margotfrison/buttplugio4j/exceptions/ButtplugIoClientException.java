package me.margotfrison.buttplugio4j.exceptions;

/**
 * The super class of all {@link Exception}s of this library
 */
public class ButtplugIoClientException extends RuntimeException {
	static final long serialVersionUID = 1L;

	public ButtplugIoClientException(String message) {
		super(message);
	}

	public ButtplugIoClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
