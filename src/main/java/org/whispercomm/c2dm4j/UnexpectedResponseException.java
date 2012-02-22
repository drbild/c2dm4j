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

import java.io.IOException;

/**
 * Signals that an unexpected response was received from the C2DM service.
 * Possible reasons include an unanticipated HTTP response status code or a
 * response body that could not be parsed.
 * 
 * @author David R. Bild
 * 
 */
public class UnexpectedResponseException extends IOException {
	private static final long serialVersionUID = -4131280524878626475L;

	/**
	 * Constructs an {@code UnexpectedResponseException} with a {@code null}
	 * message.
	 */
	public UnexpectedResponseException() {
		super();
	}

	/**
	 * Constructs an {@code UnexpectedResponseException} with the specified
	 * message and cause.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnexpectedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an {@code UnexpectedResponseException} with the specified
	 * message.
	 * 
	 * @param message
	 *            the message
	 */
	public UnexpectedResponseException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code UnexpectedResponseException} with a {@code null}
	 * message and the specified cause.
	 * 
	 * @param cause
	 */
	public UnexpectedResponseException(Throwable cause) {
		super(cause);
	}

}
