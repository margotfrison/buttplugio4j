package me.margotfrison.buttplugio4j.exceptions;

/**
 * Happen when a user attempted to send a message to the server
 * without prior required handshake has been made
 */
public class ButtplugIoClientNotHandshakedException extends ButtplugIoClientException {
	private static final long serialVersionUID = ButtplugIoClientException.serialVersionUID;

	public ButtplugIoClientNotHandshakedException(String message) {
		super(message);
	}

	public ButtplugIoClientNotHandshakedException(String message, Throwable cause) {
		super(message, cause);
	}
}
