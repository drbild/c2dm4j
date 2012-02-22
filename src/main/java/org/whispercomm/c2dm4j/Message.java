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
package org.whispercomm.c2dm4j;

import java.util.Map;

/**
 * Encapsulates a request to the C2DM service. The message includes both the
 * delivery metadata needed by C2DM (e.g., client identifier, collapse key,
 * etc.) and the key-value data to be pushed to the client. Instances are
 * immutable and can be obtained using {@link MessageBuilder}.
 * <p>
 * Full descriptions of the message fields can be found on the <a
 * href="http://code.google.com/android/c2dm/#server">C2DM web page</a>.
 * 
 * @see MessageBuilder
 * 
 * @author David R. Bild
 * 
 */
public interface Message {

	/**
	 * Gets the identifier for the client to whom the message will be sent.
	 * 
	 * @return the registration id of the client.
	 */
	public String getRegistrationId();

	/**
	 * Gets the collapse key for the message. The collapse key is used to
	 * collapse similar messages queued by the C2DM server when a device is
	 * offline. Only the last message will be delivered when the device
	 * reconnects.
	 * 
	 * @return the collapse key
	 */
	public String getCollapseKey();

	/**
	 * Returns the key-value pairs that will be delivered to the client.
	 * 
	 * @return the key-value pair payload data
	 */
	public Map<String, String> getData();

	/**
	 * Indicates if message delivery should wait until the device is active.
	 * active. If false, the device will be woken up to receive the message.
	 * 
	 * @return {@code true} if message delivery should wait until the device is
	 *         active; {@code false} if it should be delivered immediately
	 */
	public boolean delayWhileIdle();

}
