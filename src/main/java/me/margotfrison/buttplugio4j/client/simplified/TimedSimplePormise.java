package me.margotfrison.buttplugio4j.client.simplified;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TimedSimplePormise<T> extends SimplePromise<T> {
	private final long bornAt;
}
