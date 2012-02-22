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

/**
 * Encapsulates a success response from the C2DM service. This interface extends
 * {@link Response}, adding a method to access the sent message id.
 * 
 * @author David R. Bild
 * 
 */
public interface SuccessResponse extends Response {

	/**
	 * Retrieves the id of the message (to be) sent to the client.
	 * 
	 * @return the sent message id
	 */
	public String getSentMessageId();

}
