/*
 * Copyright 2012 The Regents of the University of Michigan
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

import org.whispercomm.c2dm4j.backoff.Backoff;
import org.whispercomm.c2dm4j.backoff.BackoffProvider;

/**
 * Stub {@link Backoff} implementation for testing classes that depend on a
 * {@code Backoff} object.
 * 
 * @author David R. Bild
 * 
 */
public class TestableBackoff implements Backoff {

	private boolean successRecorded;

	private boolean failureRecorded;

	private long delay;

	public TestableBackoff() {
	}

	private TestableBackoff(long delay) {
		this.delay = delay;
	}

	@Override
	public Attempt begin() {
		return new AttemptImpl(this, delay);
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public boolean successRecorded() {
		return successRecorded;
	}

	public boolean failureRecorded() {
		return failureRecorded;
	}

	private static class AttemptImpl implements Attempt {

		private final TestableBackoff backoff;
		private final long delay;

		public AttemptImpl(TestableBackoff backoff, long delay) {
			this.backoff = backoff;
			this.delay = delay;
		}

		@Override
		public long delay() {
			return delay;
		}

		@Override
		public void recordSuccess() {
			backoff.successRecorded = true;
		}

		@Override
		public void recordFailure() {
			backoff.failureRecorded = true;
		}
	}

	public static class Provider implements BackoffProvider {

		private long delay = 0L;

		private TestableBackoff mostRecent;

		@Override
		public Backoff createBackoff() {
			mostRecent = new TestableBackoff(delay);
			return mostRecent;
		}

		public void setDelay(long delay) {
			this.delay = delay;
		}

		public TestableBackoff mostRecent() {
			return mostRecent;
		}
	}

}
