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

/**
 * Implementation of {@code Backoff} in which the backoff duration increases
 * exponentially in the consecutive failure count.
 * 
 * @author David R. Bild
 * 
 */
public class ExponentialBackoff extends AbstractThreadSafeBackoff {
	private final static float DEFAULT_SCALE = 1;
	private final static int DEFAULT_MAX_COUNT = 20;

	private final float scale;

	/**
	 * Constructs a new backoff with a minimum backoff duration of 1 millisecond
	 * and a maximum duration of about about 17.5 minutes.
	 */
	public ExponentialBackoff() {
		this(DEFAULT_SCALE, DEFAULT_MAX_COUNT);
	}

	/**
	 * Constructs a new backoff with whose delay duration is computed as
	 * follows: </br> <code>
	 * scale * Math.pow(2, min(count, maxCount)) - 1.0) </br>
	 * </code> where {@code count} is the number of consecutive failures.
	 * 
	 * @param scale
	 *            scale parameter for the preceding formula
	 * @param maxCount
	 *            maxCount in the preceding formula
	 */
	public ExponentialBackoff(float scale, int maxCount) {
		super(maxCount);
		this.scale = scale;
	}

	@Override
	protected long computeInterval(int count) {
		return (long) Math.ceil(scale * (Math.pow(2, count) - 1.0));
	}

	/**
	 * Provider for constructing instances of {@code ExponentialBackoff} at
	 * runtime.
	 * 
	 * @author David R. Bild
	 * 
	 */
	public static class Provider implements BackoffProvider {
		private final float scale;
		private final int maxCount;

		/**
		 * Constructs a provider that builds backoff instances with a minimum
		 * backoff duration of 1 millisecond and a maximum duration of about
		 * about 17.5 minutes.
		 */
		public Provider() {
			this(DEFAULT_SCALE, DEFAULT_MAX_COUNT);
		}

		/**
		 * Constructs a provider that builds backoff instances whose delay
		 * durations are computed as follows: </br> <code>
		 * scale * Math.pow(2, min(count, maxCount)) - 1.0) </br>
		 * </code> where {@code count} is the number of consecutive failures.
		 * 
		 * @param scale
		 *            scale parameter for the preceding formula
		 * @param maxCount
		 *            max count parameter for the preceding formula
		 */
		public Provider(float scale, int maxCount) {
			this.scale = scale;
			this.maxCount = maxCount;
		}

		@Override
		public Backoff createBackoff() {
			return new ExponentialBackoff(scale, maxCount);
		}
	}

}
