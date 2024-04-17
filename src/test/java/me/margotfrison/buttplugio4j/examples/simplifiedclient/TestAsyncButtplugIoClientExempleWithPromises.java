package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TestAsyncButtplugIoClientExempleWithPromises {
	@Test
	public void launchTest() {
		AsyncButtplugIoClientExempleWithPromises.doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
}
