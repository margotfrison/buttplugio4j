package me.margotfrison.buttplugio4j.examples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * Theses manual tests are not to be included in
 * automatic tests.<br>
 * When performing theses tests, developers should
 * have a working buttplig.io environment setup,
 * launch the tests by commenting the {@link Disabled}
 * annotation and manually confirm the correct
 * behavior for each.<br>
 * This unsure the examples are working at all times.
 */
//@Disabled("Manual test launching example")
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
	
	@Test
	public void testAsyncButtplugIoClientExempleWithPromises() {
		new AsyncButtplugIoClientExempleWithPromises().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}

	@Test
	public void testAsyncButtplugIoClientExempleWithSubscriptions() {
		new AsyncButtplugIoClientExempleWithSubscriptions().doExample();
		try { Thread.sleep(6000); }
		catch (InterruptedException e) { }
	}

	@Test
	public void testSyncButtplugIoClientExemple() {
		new SyncButtplugIoClientExemple().doExample();
		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }
	}
}
