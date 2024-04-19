package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TestSyncButtplugIoClientExemple {
	@Test
	public void launchTest() {
		new SyncButtplugIoClientExemple().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
}
