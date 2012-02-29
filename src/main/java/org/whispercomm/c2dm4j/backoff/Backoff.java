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
 * An instance of a backoff algorithm. An attempt is started by getting an
 * {@link Attempt} object via the {@link #begin()} method. The attempt has
 * method has methods to access the remaining delay before the attempt should be
 * executed and methods to record the success or failure, which will reset or
 * increase the backoff delay state.
 * 
 * For example: </br> <code>
 * // Create the backoff object </br>
 * Backoff backoff = new TestableBackoff(); </br>
 *  </br>
 * // Begin an attempt </br>
 * Attempt attempt = new backoff.begin(); </br>
 * </br>
 * // Wait until the backoff has expired </br>
 * Thread.sleep(attempt.delay()); </br>
 *  </br>
 * // Attempt the action and record result </br>
 * boolean successful = myAction.execute(); </br>
 * if(successful) </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;attempt.recordSuccess(); </br>
 * else </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;attempt.recordFailure(); </br>
 * </code>
 * 
 * @see Attempt
 * 
 * @author David R. Bild
 * 
 */
public interface Backoff {

	/**
	 * Starts a new attempt.
	 * 
	 * @return the attempt
	 */
	public Attempt begin();

}
