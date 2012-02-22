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

import java.util.Date;

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.UnavailableResponse;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * Implementation of {@link UnavailableResponse} used by
 * {@link DefaultC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
class UnavailableResponseImpl extends ResponseImpl implements
		UnavailableResponse {

	private final Date retryAfter;

	/**
	 * Constructs a new response for the specified retry-after header and
	 * associated message.
	 * 
	 * @param retryAfter
	 *            the retry-after header value
	 * @param message
	 *            the message for which this is a response
	 */
	public UnavailableResponseImpl(Date retryAfter, Message message) {
		super(ResponseType.ServiceUnavailable, message);
		this.retryAfter = retryAfter;
	}

	/**
	 * Constructs a new response for the specified retry-after header,
	 * associated message, and updated authentication token.
	 * 
	 * @param retryAfter
	 *            the retry-after header value
	 * @param message
	 *            the message for which this is a response
	 * @param authToken
	 *            the updated authentication token
	 */
	public UnavailableResponseImpl(Date retryAfter, Message message,
			AuthToken authToken) {
		super(ResponseType.ServiceUnavailable, message, authToken);
		this.retryAfter = retryAfter;
	}

	@Override
	public boolean hasRetryAfter() {
		return (retryAfter != null);
	}

	@Override
	public Date retryAfter() {
		return retryAfter;
	}

	@Override
	public String toString() {
		return String
				.format("UnavailableResponseImpl(type=%s, retryAfter=\"%s\", hasUpdatedAuthToken=%b, message=%s)",
						this.getResponseType(), this.retryAfter(),
						this.hasUpdatedAuthToken(), this.getMessage());
	}

}
