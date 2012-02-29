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

/**
 * A class that represents one attempt of an action that may need to backoff.
 * The class has methods to determine the remaining backoff duration before the
 * attempt may be executed and record the success or failure of the attempt (to
 * reset or increase the backoff delay for future attempts).
 * 
 * @see Backoff
 * 
 * @author David R. Bild
 * 
 */
public interface Attempt {

	/**
	 * Gets the remaining delay in milliseconds. The attempt should not be
	 * started until the remaining delay is zero.
	 * 
	 * @return the remaining delay in milliseconds.
	 */
	public long delay();

	/**
	 * Records that the most recent action attempt by this thread succeeded.
	 * This call will reset the backoff counter if needed.
	 */
	public void recordSuccess();

	/**
	 * Records that the most recent action attempt by this thread failed. This
	 * call will increment the backoff counter if needed.
	 */
	public void recordFailure();

}
