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
package org.whispercomm.c2dm4j.async.handler;

import org.whispercomm.c2dm4j.backoff.ExponentialBackoff;

/**
 * Static factory methods to construct {@link AsyncHandlers} instances.
 * 
 * @author David R. Bild
 * 
 */
public class AsyncHandlersFactory {

	/**
	 * Should not be instantiated.
	 */
	private AsyncHandlersFactory() {
		throw new IllegalStateException();
	}

	/**
	 * Creates a new {@code AsyncHandlers} instance with two registered
	 * handlers: {@link DeviceBackoffThrottle} and {@link GlobalBackoffThrottle},
	 * both using the default {@link ExponentialBackoff} parameters.
	 * 
	 * @return the handlers instance with the default throttles registered
	 */
	public static AsyncHandlers create() {
		AsyncHandlers handlers = createEmpty();

		new GlobalBackoffThrottle(new ExponentialBackoff(), handlers);
		new DeviceBackoffThrottle(new ExponentialBackoff.Provider(), handlers);

		return handlers;
	}

	/**
	 * Creates a new {@code AsyncHandlers} instance with no handlers or filters
	 * registered.
	 * 
	 * @return the empty async handlers instance.
	 */
	public static AsyncHandlers createEmpty() {
		return new AsyncHandlersImpl();
	}

}
