package me.margotfrison.buttplugio4j.examples;

import lombok.SneakyThrows;

class ExempleUtils {
	@SneakyThrows
	static void waitFor(long millis) {
		Thread.sleep(millis);
	}
}
