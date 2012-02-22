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

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.auth.AuthToken;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link UnavailableResponseImpl}.
 * 
 * @author David R. Bild
 * 
 */
public class UnavailableResponseImplTest {
	private static final String REGISTRATION_ID = "My Registration Id";
	private static final String COLLAPSE_KEY = "My Collapse Key";
	private static final String AUTH_TOKEN = "My Auth Token";

	private Message message;

	private AuthToken token;

	private Date date;

	@Before
	public void setup() {
		message = new MessageBuilder().registrationId(REGISTRATION_ID)
				.collapseKey(COLLAPSE_KEY).build();
		token = new AuthToken(AUTH_TOKEN);
		date = new Date();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorRejectsNullMessage() {
		new UnavailableResponseImpl(date, null);
	}

	@Test
	public void getResponseTypeReturnsSuccessType() {
		assertThat(
				new UnavailableResponseImpl(date, message).getResponseType(),
				is(ResponseType.ServiceUnavailable));
	}

	@Test
	public void hasRetryAfternReturnsTrueIfConstructorSuppliedDate() {
		assertThat(new UnavailableResponseImpl(date, message).hasRetryAfter(),
				is(true));
	}

	@Test
	public void getRetryAfterReturnsConstructorSuppliedDate() {
		assertThat(new UnavailableResponseImpl(date, message).retryAfter(),
				is(date));
	}

	@Test
	public void hasRetryAfterReturnsFalseIfConstructorNotSuppliedDate() {
		assertThat(new UnavailableResponseImpl(null, message).hasRetryAfter(),
				is(false));
	}

	@Test
	public void getRetryAfterReturnsNullIfConstructorNotSuppliedDate() {
		assertThat(new UnavailableResponseImpl(null, message).retryAfter(),
				is(nullValue()));
	}

	@Test
	public void getMessageReturnsConstructorSuppliedMessage() {
		assertThat(new UnavailableResponseImpl(date, message).getMessage(),
				is(message));
	}

	@Test
	public void hasUpdatedAuthTokenIsFalseWhenNotSuppliedOrNull() {
		assertThat(
				new UnavailableResponseImpl(date, message)
						.hasUpdatedAuthToken(),
				is(false));
		assertThat(
				new UnavailableResponseImpl(date, message, null)
						.hasUpdatedAuthToken(),
				is(false));
	}

	@Test
	public void hasUpdatedAuthTokenIsTrueWhenSupplied() {
		assertThat(
				new UnavailableResponseImpl(date, message, token)
						.hasUpdatedAuthToken(),
				is(true));
	}

	@Test
	public void getUpdatedAuthTokenReturnsSuppliedToken() {
		assertThat(
				new UnavailableResponseImpl(date, message, token)
						.getUpdatedAuthToken(),
				is(token));
	}

}
