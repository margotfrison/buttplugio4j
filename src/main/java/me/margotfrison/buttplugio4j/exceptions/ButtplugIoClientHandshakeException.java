package me.margotfrison.buttplugio4j.exceptions;

/**
 * Happen when the handshake with the server have failed
 */
public class ButtplugIoClientHandshakeException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientHandshakeException(String message) {
		super(message);
	}

	public ButtplugIoClientHandshakeException(String message, Throwable cause) {
		super(message, cause);
	}
}
