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

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link AuthToken}.
 * 
 * @author David R. Bild
 * 
 */
public class AuthTokenTest {

	private static final String TOKEN_ONE = "My First Token String";
	private static final String TOKEN_TWO = "My Second Token String";

	@Test(expected = IllegalArgumentException.class)
	public void constructionWithNullStringThrowsException() {
		new AuthToken(null);
	}

	@Test
	public void toStringReturnsTheConstructorSuppliedTokenString() {
		AuthToken token = new AuthToken(TOKEN_ONE);
		assertThat(token.toString(), is(TOKEN_ONE));
	}

	@Test
	public void tokenDoesNotEqualTokenWithDifferentString() {
		AuthToken token = new AuthToken(TOKEN_ONE);
		AuthToken diff = new AuthToken(TOKEN_TWO);
		assertThat(token, is(not(diff)));
	}

	@Test
	public void tokenEqualsTokenWithSameString() {
		AuthToken token = new AuthToken(TOKEN_ONE);
		AuthToken same = new AuthToken(TOKEN_ONE);
		assertThat(token, is(same));
	}

}
