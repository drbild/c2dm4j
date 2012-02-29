/*
 * Copyright 2012 The University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.whispercomm.c2dm4j.backoff;

import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract implementation of a threadsafe {@code Backoff} object. With
 * multiple threads accessing the backoff object, multiple {@link Attempt}
 * instances are active and can interfere with each other. E.g., the backoff
 * counter might be increment once for each concurrent attempt (all starting
 * with the same backoff), when it should really be incremented once. This class
 * allows only the first attempt to respond for a given state to change the
 * backoff state.
 * <p>
 * For example, if two concurrent attempts both have a backoff of 10 seconds and
 * both fail, the backoff will only be increased (e.g., to 20 seconds) by the
 * first one to respond. The second one failing will not further increase the
 * delay to 40 seconds. An attempt starting with the 20 second delay could, of
 * course, increase the delay to 40 seconds upon failure.
 * <p>
 * Implementing children must override the {@link #computeInterval(int)} method
 * to convert the internal failure count to an absolute backoff duration.
 * 
 * @author David R. Bild
 * 
 */
public abstract class AbstractThreadSafeBackoff implements Backoff {

	private static final State NO_BACKOFF = new State(0, 0);

	private final int maxCount;

	private AtomicReference<State> state;

	/**
	 * Constructs a new backoff with a maximum backoff count.
	 * 
	 * @param maxCount
	 */
	protected AbstractThreadSafeBackoff(int maxCount) {
		this.maxCount = maxCount;
		this.state = new AtomicReference<State>(NO_BACKOFF);
	}

	/**
	 * Computes an absolute backoff duration from the given count of consecutive
	 * failures.
	 * 
	 * @param count
	 *            number of consecutive failures
	 * @return the backoff duration
	 */
	protected abstract long computeInterval(int count);

	@Override
	public Attempt begin() {
		return new AttemptImpl(this, state.get());
	}

	/**
	 * Records that the attempt succeeded, reseting the failure count. Has no
	 * effect if {@link #recordFailure(State)} or {@link #recordSuccess(State)}
	 * was previously called on an {@code Attempt} with the same backoff state.
	 * 
	 * @param oldState
	 */
	public void recordSuccess(State oldState) {
		state.compareAndSet(oldState, NO_BACKOFF);
	}

	/**
	 * Records that the attempt failed, incrementing the failure count. Has no
	 * effect if {@link #recordFailure(State)} or {@link #recordSuccess(State)}
	 * was previously called on an {@code Attempt} with the same backoff state.
	 * 
	 * @param oldState
	 */
	public void recordFailure(State oldState) {
		state.compareAndSet(oldState, increment(oldState));
	}

	/**
	 * Increments the failure count (up to the specified maximum) and computes
	 * the new expiry time.
	 * 
	 * @param state
	 *            the current state
	 * @return the incremented state
	 */
	private State increment(State state) {
		int count = state.count();
		count = count < maxCount ? count + 1 : count;
		return new State(count, System.currentTimeMillis()
				+ computeInterval(count));
	}

	/**
	 * The state of the backoff algorithm as a failure count and expiry time.
	 * 
	 * @author David R. Bild
	 * 
	 */
	private static class State {
		private final int count;
		private final long expiry;

		public State(int count, long expiry) {
			this.count = count;
			this.expiry = expiry;
		}

		public int count() {
			return count;
		}

		public long expiry() {
			return expiry;
		}
	}

	/**
	 * Implementation of {@code Attempt} returned by
	 * {@link AbstractThreadSafeBackoff#begin()}.
	 * 
	 * @author David R. Bild
	 * 
	 */
	private static class AttemptImpl implements Attempt {
		private final AbstractThreadSafeBackoff backoff;

		private final State state;

		public AttemptImpl(AbstractThreadSafeBackoff backoff, State state) {
			this.backoff = backoff;
			this.state = state;
		}

		@Override
		public long delay() {
			long expiry = state.expiry();
			long current = System.currentTimeMillis();
			return (expiry < current) ? 0 : expiry - current;
		}

		@Override
		public void recordSuccess() {
			backoff.recordSuccess(state);
		}

		@Override
		public void recordFailure() {
			backoff.recordFailure(state);
		}
	}

}
