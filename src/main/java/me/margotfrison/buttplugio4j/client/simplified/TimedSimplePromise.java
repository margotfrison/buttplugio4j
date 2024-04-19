package me.margotfrison.buttplugio4j.client.simplified;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TimedSimplePromise<T> extends SimplePromise<T> {
	private final long bornAt;
}
