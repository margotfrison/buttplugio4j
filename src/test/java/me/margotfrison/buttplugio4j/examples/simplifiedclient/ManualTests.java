package me.margotfrison.buttplugio4j.examples.simplifiedclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

@Disabled("Manual test launching example")
public class ManualTests {
	@BeforeEach
	@SneakyThrows
	public void delay() {
		Thread.sleep(1000);
	}

	@Test
	public void testBasicButtplugIoClientExemple() {
		new BasicButtplugIoClientExemple().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
	
	//@Test
	public void testAsyncButtplugIoClientExempleWithPromises() {
		new AsyncButtplugIoClientExempleWithPromises().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}

	//@Test
	public void testAsyncButtplugIoClientExempleWithSubscriptions() {
		new AsyncButtplugIoClientExempleWithSubscriptions().doExample();
		try { Thread.sleep(6000); }
		catch (InterruptedException e) { }
	}

	//@Test
	public void testSyncButtplugIoClientExemple() {
		new SyncButtplugIoClientExemple().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
}
