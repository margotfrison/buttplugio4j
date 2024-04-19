package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TestAsyncButtplugIoClientExempleWithSubscriptions {
	@Test
	public void launchTest() {
		new AsyncButtplugIoClientExempleWithSubscriptions().doExample();
		try { Thread.sleep(15000); }
		catch (InterruptedException e) { }
	}
}
