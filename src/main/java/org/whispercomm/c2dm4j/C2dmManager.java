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
package org.whispercomm.c2dm4j;

import java.io.IOException;

import org.whispercomm.c2dm4j.auth.AuthTokenException;

import org.whispercomm.c2dm4j.async.AsyncC2dmManager;
import org.whispercomm.c2dm4j.impl.DefaultC2dmManager;

/**
 * An interface for pushing messages to clients via the C2DM service.
 * Configuration details (e.g., providing the authentication token) are
 * implementation-dependent.
 * <p>
 * Implementations do not implement automatic retry, exponential back off, or
 * attempt to honor <code>Retry-After</code> headers. For many applications, the
 * asynchronous manager {@link AsyncC2dmManager}, which implements these
 * features on top of a {@code C2dmManager} instance, will be more appropriate.
 * 
 * @see DefaultC2dmManager
 * @see AsyncC2dmManager
 * 
 * @author David R. Bild
 * 
 */
public interface C2dmManager {

	/**
	 * Sends a message to the C2DM service to be delivered to the client
	 * specified in the message header.
	 * 
	 * @param msg
	 *            the message to deliver
	 * @return the response from the C2DM service
	 * @throws UnexpectedResponseException
	 *             if the C2DM service response could not be parsed
	 * @throws AuthTokenException
	 *             if authentication token could not be retrieved
	 * @throws IOException
	 *             if unable to communicate with the C2DM service
	 */
	public Response pushMessage(Message msg)
			throws UnexpectedResponseException, AuthTokenException, IOException;

}
