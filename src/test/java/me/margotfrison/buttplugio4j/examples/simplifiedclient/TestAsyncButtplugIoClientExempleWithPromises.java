package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Manual test launching example")
public class TestAsyncButtplugIoClientExempleWithPromises {
	@Test
	public void launchTest() {
		new AsyncButtplugIoClientExempleWithPromises().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
}
