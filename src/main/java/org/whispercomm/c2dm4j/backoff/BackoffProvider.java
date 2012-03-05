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
 * Provider to create {@link Backoff} instances. This is useful when instances
 * must be created dynamically at runtime, but the creating code does not know
 * all the parameters for the instances. Instead, pass in a configured
 * {@code BackoffProvider} instance to code that needs to create {@code Backoff}
 * objects.
 * 
 * @author David R. Bild
 * 
 */
public interface BackoffProvider {

	/**
	 * Create a new configured backoff object.
	 * 
	 * @return the new backoff object
	 */
	Backoff createBackoff();
}
