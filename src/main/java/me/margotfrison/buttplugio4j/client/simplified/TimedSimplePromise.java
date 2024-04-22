package me.margotfrison.buttplugio4j.client.simplified;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A timed promise is a {@link Promise} that tracks its
 * born time {@link TimedSimplePromise#bornAt}.<br>
 * It allows, for example, it's user to define a timeout
 * and fail the {@link Promise} if it exceeds it.
 * @param <T>
 */
@RequiredArgsConstructor
@Getter
public class TimedSimplePromise<T> extends SimplePromise<T> {
	private final long bornAt;
}
