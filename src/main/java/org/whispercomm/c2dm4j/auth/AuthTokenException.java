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
package org.whispercomm.c2dm4j.auth;

import java.io.IOException;

/**
 * Signals a problem retrieving or persisting an authentication token. Often
 * this will be due to an IO error with the {@link AuthTokenProvider}'s
 * persistence mechanism.
 * 
 * @author David R. Bild
 * 
 */
public class AuthTokenException extends IOException {
	private static final long serialVersionUID = 5667251548135290053L;

	/**
	 * Constructs an {@code AuthTokenException} will a {@code null} error
	 * message.
	 */
	public AuthTokenException() {
		super();
	}

	/**
	 * Constructs an {@code AuthTokenException} with the specified error message
	 * and cause.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the cause
	 */
	public AuthTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an {@code AuthTokenException} with the specified error
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public AuthTokenException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code AuthTokenException} with the specified cause and a
	 * {@code null} error message.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AuthTokenException(Throwable cause) {
		super(cause);
	}

}
