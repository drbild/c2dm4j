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
package org.whispercomm.c2dm4j.impl;

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.SuccessResponse;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * Implementation of {@link SuccessResponse} used by {@link DefaultC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
class SuccessResponseImpl extends ResponseImpl implements SuccessResponse {

	private final String sentMessageId;

	/**
	 * Constructs a new response with the specified sent message id and
	 * associated message.
	 * 
	 * @param sentMessageId
	 *            the sent message id
	 * @param message
	 *            the message for which this is a response
	 */
	public SuccessResponseImpl(String sentMessageId, Message message) {
		super(ResponseType.Success, message);
		this.sentMessageId = validateSentMessageId(sentMessageId);
	}

	/**
	 * Constructs a new response with the specified sent message id, associated
	 * message, and updated authentication token.
	 * 
	 * @param sentMessageId
	 *            the sent message id
	 * @param message
	 *            the message for which this is a response
	 * @param authToken
	 *            the update authentication token
	 */
	public SuccessResponseImpl(String sentMessageId, Message message,
			AuthToken authToken) {
		super(ResponseType.Success, message, authToken);
		this.sentMessageId = validateSentMessageId(sentMessageId);
	}

	private String validateSentMessageId(String sentMessageId) {
		if (sentMessageId == null)
			throw new IllegalArgumentException(
					"Argument 'sentMessageId' may not be null.");

		return sentMessageId;
	}

	@Override
	public String getSentMessageId() {
		return sentMessageId;
	}

	@Override
	public String toString() {
		return String
				.format("SuccessResponseImpl(type=%s, sentMessageId=%s, hasUpdatedAuthToken=%b, message=%s)",
						this.getResponseType(), this.getSentMessageId(),
						this.hasUpdatedAuthToken(), this.getMessage());
	}
}
