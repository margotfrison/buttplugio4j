package me.margotfrison.buttplugio4j.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.margotfrison.buttplugio4j.exceptions.ButtplugIoClientInitException;

/**
 * Class with static methods used to parse a collection of messages into a JSON
 * format readable by the buttplug.io server and vice versa.<br>
 * See the <a href="https://buttplug-spec.docs.buttplug.io/docs/spec/messages">
 * official documentation of buttplug.io messages mechanism</a> to have a glance
 * of how this class work.
 */
public class MessageJsonParser {
	private static final Gson gson = new GsonBuilder().create();
	private static final JsonParser jsonParser = new JsonParser();
	// Find all Message classes and their type
	private static final Map<String, Class<? extends Message>> MESSAGE_TYPE_TO_CLASS =
			new Reflections(Message.class.getPackageName()).getSubTypesOf(Message.class).stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.collect(Collectors.toMap(
						(clazz) -> {
							try {
								Constructor<? extends Message> constructor = clazz.getDeclaredConstructor();
								constructor.setAccessible(true);
								return constructor.newInstance().getType();
							} catch (Exception e) {
								throw new ButtplugIoClientInitException("Unable to process Message classes for the JSON parser", e);
							}
						}, Function.identity()));

	/**
	 * Parse a {@link Message} into this JSON format :
	 * {
	 *   "MessageType" :
	 *   {
	 *     "MessageField1": "MessageData1",
	 *     "MessageField2": "MessageData2"
	 *   }
	 * }
	 * with "MessageType" corresponding to the class name of the {@link Message}.
	 * @param message the {@link Message} to parse into JSON
	 * @return a string representation of the {@link Message} in JSON
	 */
	private static JsonElement toJson(Message message) {
		JsonObject element = new JsonObject();
		element.add(message.getType(), gson.toJsonTree(message));
		return element;
	}

	/**
	 * Transform a list of {@link Message}s into JSON format readable by
	 * the buttplug.io server.
	 * @param messages the list of {@link Message}s to parse into JSON
	 * @return a string representation of the list of {@link Message}s in JSON
	 */
	public static String toJson(Collection<Message> messages) {
		JsonArray jsonArray = new JsonArray();
		for (Message message : messages)
			jsonArray.add(toJson(message));
		return jsonArray.toString();
	}

	/**
	 * Parse this JSON format :
	 * {
	 *   "MessageType" :
	 *   {
	 *     "MessageField1": "MessageData1",
	 *     "MessageField2": "MessageData2"
	 *   }
	 * }
	 * into a {@link Message} with the corresponding class.
	 * @param element the JSON element to parse into {@link Message}
	 * @return a {@link Message} object parsed from the JSON
	 */
	private static Message fromJson(JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		Entry<String, JsonElement> entry = jsonObject.entrySet().iterator().next();
		Class<? extends Message> clazz = MESSAGE_TYPE_TO_CLASS.get(entry.getKey());
		return gson.fromJson(entry.getValue(), clazz);
	}

	/**
	 * Transform a JSON sent by the server into a list of {@link Message}s.
	 * @param json the JSON {@link String} representation to parse into {@link Message}
	 * @return a list of {@link Message}s parsed from JSON
	 */
	public static Collection<Message> fromJson(String json) throws Exception {
		JsonArray jsonArray = jsonParser.parse(json).getAsJsonArray();
		List<Message> messages = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++)
			messages.add(fromJson(jsonArray.get(i)));
		return messages;
	}
}
