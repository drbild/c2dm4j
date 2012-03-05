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

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.auth.AuthToken;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ResponseImpl}.
 * 
 * @author David R. Bild
 * 
 */
public class ResponseImplTest {
	private static final String REGISTRATION_ID = "My Registration Id";
	private static final String COLLAPSE_KEY = "My Collapse Key";
	private static final String AUTH_TOKEN = "My Auth Token";

	private Message message;

	private AuthToken token;

	@Before
	public void setup() {
		message = new MessageBuilder().registrationId(REGISTRATION_ID)
				.collapseKey(COLLAPSE_KEY).build();
		token = new AuthToken(AUTH_TOKEN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorRejectsNullMessage() {
		new ResponseImpl(ResponseType.DeviceQuotaExceeded, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorRejectsNullResponseType() {
		new ResponseImpl(null, message);
	}

	@Test
	public void getResponseTypeReturnsConstructorSuppliedType() {
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message).getResponseType(),
				is(ResponseType.DeviceQuotaExceeded));
		assertThat(new ResponseImpl(ResponseType.InvalidRegistration,
				message).getResponseType(),
				is(ResponseType.InvalidRegistration));
	}

	@Test
	public void getMessageReturnsConstructorSuppliedMessage() {
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message).getMessage(), is(message));
	}

	@Test
	public void hasUpdatedAuthTokenIsFalseWhenNotSuppliedOrNull() {
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message).hasUpdatedAuthToken(), is(false));
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message, null).hasUpdatedAuthToken(), is(false));
	}

	@Test
	public void hasUpdatedAuthTokenIsTrueWhenSupplied() {
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message, token).hasUpdatedAuthToken(), is(true));
	}

	@Test
	public void getUpdatedAuthTokenReturnsSuppliedToken() {
		assertThat(new ResponseImpl(ResponseType.DeviceQuotaExceeded,
				message, token).getUpdatedAuthToken(), is(token));
	}
}
