package me.margotfrison.buttplugio4j.protocol;/**
*
*/

import lombok.Getter;
import lombok.ToString;

/**
 * See the <a href="https://buttplug-spec.docs.buttplug.io/docs/spec/messages">
 * official documentation</a> to have a glance of what this whole package do.<br>
 * But to present my implementation :
 * <ul>This {@link Message} class is the super class of any message sent and
 * received by the server. The id of each message is incremented automatically.</ul>
 * <ul>To try to avoid to overcomplicate things. I chose to delegate the JSON
 * formating of {@link Message}s to the class {@link MessageJsonParser}.
 * This allows me to concatenate a list of {@link Message}s (as the official
 * documentation allows) without the need of creating an overcomplicated Java
 * object mesh.</ul>
 * @see MessageJsonParser
 */
@ToString
@Getter
public abstract class Message {
	/**
	 * This is the last version of buttplug.io supported.
	 * It is recommended to use this value.
	 */
	public static final int LAST_SUPPORTED_VERSION = 3;

	private static int idCount = 1;
	private int Id = idCount++;

	public abstract String getType();
}
