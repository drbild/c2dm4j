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
 * An abstract implementation of the {@link AuthTokenProvider} interface. This
 * implementation caches the current token for fast {@link #getAuthToken()}
 * calls and provides proper locking to be thread-safe.
 * <p>
 * Child classes must provide the persistence layer by implementing the
 * {@link #readToken()} and {@link #writeToken(AuthToken)} methods.
 * 
 * @author David R. Bild
 * 
 */
public abstract class AbstractAuthTokenProvider implements AuthTokenProvider {

	private volatile AuthToken cachedToken;

	/**
	 * Gets the current authentication token.
	 * <p>
	 * This method is not guaranteed to be called after each
	 * {@link #writeToken(AuthToken)} call, because this class implements
	 * authentication token caching.
	 * 
	 * @return the current authentication token.
	 * @throws IOException
	 *             if the current token could not be retrieved.
	 */
	abstract protected AuthToken readToken() throws IOException;

	/**
	 * Persists the new authentication token.
	 * 
	 * @param token
	 *            the new authentication token.
	 * @throws IOException
	 *             if the new token could not be persisted.
	 */
	abstract protected void writeToken(AuthToken token) throws IOException;

	@Override
	public AuthToken getAuthToken() throws AuthTokenException {
		if (cachedToken == null)
			try {
				cachedToken = readToken();
			} catch (IOException e) {
				throw new AuthTokenException("Failed to read token.", e);
			}

		return cachedToken;
	}

	@Override
	public synchronized void updateAuthToken(AuthToken authToken)
			throws AuthTokenException {
		if (cachedToken == null) {
			getAuthToken();
		}

		if (!cachedToken.equals(authToken)) {
			try {
				writeToken(authToken);
				cachedToken = authToken;
			} catch (IOException e) {
				throw new AuthTokenException("Failed to write token.", e);
			}
		}
	}

}
