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

import org.whispercomm.c2dm4j.auth.AbstractAuthTokenProvider;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * Implementation of {@link AbstractAuthTokenProvider} for testing purposes.
 * <p>
 * Stores the current token in memory only. To enable testing of
 * exception-handling, {@link TestableAuthTokenProvider#shouldThrowException
 * shouldThrowException(true)} indicates that the {@link #readToken} and
 * {@link #writeToken} methods should throw an {@link IOException} instead of
 * succeeding.
 * 
 * @author David R. Bild
 * 
 */
public class TestableAuthTokenProvider extends AbstractAuthTokenProvider {

	private AuthToken token;

	private boolean throwException;

	private int readCount;

	private int writeCount;

	public TestableAuthTokenProvider(String initialToken) {
		this.token = new AuthToken(initialToken);
		throwException = false;
		readCount = 0;
		writeCount = 0;
	}

	public TestableAuthTokenProvider(AuthToken initialToken) {
		this(initialToken.toString());
	}

	/**
	 * Indicates if future calls to {@link readToken} and {@link writeToken}
	 * should throw an {@link IOException}.
	 * 
	 * @param shouldThrow
	 *            true if future calls should throw exceptions.
	 */
	public void shouldThrowException(boolean shouldThrow) {
		this.throwException = shouldThrow;
	}

	@Override
	protected AuthToken readToken() throws IOException {
		++readCount;
		if (throwException)
			throw new IOException(
					"Triggered by call to shouldThrowException(true).");
		else
			return this.token;
	}

	@Override
	protected void writeToken(AuthToken token) throws IOException {
		++writeCount;
		if (throwException)
			throw new IOException(
					"Triggered by call to shouldThrowException(true).");
		else
			this.token = token;
	}

	/**
	 * @return the number of times {@link #readToken} was called.
	 */
	public int getReadCount() {
		return readCount;
	}

	/**
	 * Resets to zero the counter for the number of times {@link #readToken} was
	 * called.
	 */
	public void resetReadCount() {
		readCount = 0;
	}

	/**
	 * @return the numbers of times {@link #writeToken} was called.
	 */
	public int getWriteCount() {
		return writeCount;
	}

	/**
	 * Resets to zero the counter for the number of times {@link #writeToken}
	 * was called.
	 */
	public void resetWriteCount() {
		writeCount = 0;
	}
}
