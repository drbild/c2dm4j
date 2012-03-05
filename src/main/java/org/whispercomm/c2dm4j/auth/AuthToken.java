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

/**
 * An authentication token for the Google ClientLogin API, used for
 * authentication with the C2DM servers.
 * 
 * @see AuthTokenProvider
 * 
 * @author David R. Bild
 * 
 */
public class AuthToken {

	private final String token;

	/**
	 * Constructs a new immutable authentication token.
	 * 
	 * @param token
	 *            the token material.
	 */
	public AuthToken(String token) {
		if (token == null)
			throw new IllegalArgumentException(
					"Argument 'token' may not be null.");

		this.token = token;
	}

	@Override
	public String toString() {
		return token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthToken other = (AuthToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

}
