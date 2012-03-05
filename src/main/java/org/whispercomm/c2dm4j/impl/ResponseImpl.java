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
package org.whispercomm.c2dm4j.impl;

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * Implementation of {@link Response} used by {@link DefaultC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
class ResponseImpl implements Response {

	private final ResponseType type;

	private final Message message;

	private final AuthToken authToken;

	/**
	 * Constructs a new response for the specified type and associated message.
	 * 
	 * @param type
	 *            response type
	 * @param message
	 *            message for which this is a response
	 */
	public ResponseImpl(ResponseType type, Message message) {
		super();
		this.type = validateType(type);
		this.message = validateMessage(message);
		this.authToken = null;
	}

	/**
	 * Constructs a new response for the specified type, associated message, and
	 * updated authentication token.
	 * 
	 * @param type
	 *            response type
	 * @param message
	 *            message for which this is a reponse
	 * @param authToken
	 *            updated authentication token
	 */
	public ResponseImpl(ResponseType type, Message message, AuthToken authToken) {
		super();
		this.type = validateType(type);
		this.message = validateMessage(message);
		this.authToken = authToken;
	}

	private ResponseType validateType(ResponseType type) {
		if (type == null)
			throw new IllegalArgumentException(
					"Argument 'type' may not be null.");

		return type;
	}

	private Message validateMessage(Message message) {
		if (message == null)
			throw new IllegalArgumentException(
					"Argument 'message' may not be null.");

		return message;
	}

	@Override
	public ResponseType getResponseType() {
		return type;
	}

	@Override
	public Message getMessage() {
		return message;
	}

	@Override
	public boolean hasUpdatedAuthToken() {
		return (authToken != null);
	}

	@Override
	public AuthToken getUpdatedAuthToken() {
		return authToken;
	}

	@Override
	public String toString() {
		return String
				.format("DefaultResponseImpl(type=%s, hasUpdatedAuthToken=%b, message=%s)",
						this.getResponseType(), this.hasUpdatedAuthToken(),
						this.getMessage());
	}
}
