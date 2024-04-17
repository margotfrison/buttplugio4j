package me.margotfrison.buttplugio4j.client.simplified;

import lombok.NonNull;

public class RepeatedPromise<T> extends AbstractPromise<T> {
	public RepeatedPromise() { }

	@NonNull
	public void finish() {
		done = true;
	}
}
