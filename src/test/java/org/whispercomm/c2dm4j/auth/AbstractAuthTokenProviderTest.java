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

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.auth.AbstractAuthTokenProvider;
import org.whispercomm.c2dm4j.auth.AuthToken;
import org.whispercomm.c2dm4j.auth.AuthTokenException;
import org.whispercomm.c2dm4j.auth.AuthTokenProvider;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link AbstractAuthTokenProvider}.
 * 
 * @author David R. Bild
 * 
 */
public class AbstractAuthTokenProviderTest {

	private static final AuthToken TOKEN_ONE = new AuthToken("My First Token");
	private static final AuthToken TOKEN_TWO = new AuthToken("My Second Token");

	/*
	 * factoryImpl and factory should reference the same object. The different
	 * interfaces are two easily separate the configuration methods (accessed
	 * using factoryImpl) from the tested methods (accessed using factory).
	 */
	private AuthTokenProvider provider;
	private TestableAuthTokenProvider providerImpl;

	@Before
	public void setup() {
		providerImpl = new TestableAuthTokenProvider(TOKEN_ONE);
		provider = providerImpl;
		providerImpl.shouldThrowException(false);
	}

	@Test
	public void getAuthTokenReturnsReadToken() throws AuthTokenException {
		assertThat(provider.getAuthToken(), is(TOKEN_ONE));
	}

	@Test
	public void getAuthTokenCachesReadToken() throws AuthTokenException {
		assertThat(provider.getAuthToken(), is(TOKEN_ONE)); // Read token to
															// prime the cache
		providerImpl.resetReadCount();
		assertThat(provider.getAuthToken(), is(TOKEN_ONE)); // Read token from
															// cache
		assertThat(providerImpl.getReadCount(), is(0));
	}

	@Test
	public void writeAuthTokenPersistsChangedTokenViaWriteToken()
			throws AuthTokenException {
		providerImpl.resetWriteCount();
		provider.updateAuthToken(TOKEN_TWO);
		assertThat(providerImpl.getWriteCount(), is(1));
	}

	@Test
	public void writeAuthTokenDoesNotPersistUnchangedTokenViaWriteToken()
			throws AuthTokenException {
		providerImpl.resetWriteCount();
		provider.updateAuthToken(TOKEN_ONE);
		assertThat(providerImpl.getWriteCount(), is(0));
	}

	@Test
	public void changedTokenIsCachedAndReturnedBySubsequentGets()
			throws AuthTokenException {
		provider.updateAuthToken(TOKEN_TWO);
		providerImpl.resetReadCount();
		assertThat("changed token is returned", provider.getAuthToken(),
				is(TOKEN_TWO));
		assertThat("changed token was cached", providerImpl.getReadCount(),
				is(0));
	}

	@Test(expected = AuthTokenException.class)
	public void getAuthTokenThrowsExceptionsOnSubclassException()
			throws AuthTokenException {
		providerImpl.shouldThrowException(true);
		provider.getAuthToken();
	}

	@Test(expected = AuthTokenException.class)
	public void writeAuthTokenThrowsExceptionOnSubclassException()
			throws AuthTokenException {
		providerImpl.shouldThrowException(true);
		provider.updateAuthToken(TOKEN_TWO);
	}

}
