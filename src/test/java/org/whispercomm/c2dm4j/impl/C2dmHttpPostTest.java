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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.auth.AuthToken;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link C2dmHttpPost}.
 * 
 * @author David R. Bild
 * 
 */

public class C2dmHttpPostTest {
	private static final String REGISTRATION_ID = "My Registration Id";
	private static final String COLLAPSE_KEY = "My Collapse Key";
	private static final String DATA_KEY = "mykey";
	private static final String DATA_VALUE = "myvalue";
	private static final String AUTH_TOKEN = "My Auth Token";

	private URI uri;

	private Message message;

	private AuthToken token;

	private C2dmHttpPost post;

	@Before
	public void setup() throws URISyntaxException {
		message = new MessageBuilder().registrationId(REGISTRATION_ID)
				.collapseKey(COLLAPSE_KEY).delayWhileIdle(true)
				.put(DATA_KEY, DATA_VALUE).build();
		token = new AuthToken(AUTH_TOKEN);
		uri = new URI("https://my.test.com/my/test/");

		post = new C2dmHttpPost(message, token, uri);
	}

	@Test
	public void uriIsSet() {
		assertThat(post.getURI(), is(uri));
	}

	@Test
	public void authTokenHeaderIsSet() {
		String authTokenHeaderValue = String.format("GoogleLogin auth=%s",
				token);

		assertThat(post.containsHeader("Authorization"), is(true));
		assertThat(post.getFirstHeader("Authorization").getValue(),
				is(authTokenHeaderValue));
	}

	@Test
	public void entityIsFormEncoded() {
		assertThat(post.getEntity().getContentType().getValue(),
				is(String.format(
						"application/x-www-form-urlencoded; charset=%s",
						HTTP.DEFAULT_CONTENT_CHARSET)));
	}

	@Test
	public void entityContainsRegistrationId() throws IOException {
		List<NameValuePair> data = URLEncodedUtils.parse(post.getEntity());
		assertThat(data.contains(new BasicNameValuePair("registration_id",
				REGISTRATION_ID)), is(true));
	}

	@Test
	public void entityContainsCollapseKey() throws IOException {
		List<NameValuePair> data = URLEncodedUtils.parse(post.getEntity());
		assertThat(data.contains(new BasicNameValuePair("collapse_key",
				COLLAPSE_KEY)), is(true));
	}

	@Test
	public void entityContainsDelayWhileIdle() throws IOException {
		List<NameValuePair> data = URLEncodedUtils.parse(post.getEntity());
		assertThat(
				data.contains(new BasicNameValuePair("delay_while_idle", null)),
				is(true));
	}

	@Test
	public void entityContainsDataPairs() throws IOException {
		List<NameValuePair> data = URLEncodedUtils.parse(post.getEntity());
		assertThat(data.contains(new BasicNameValuePair(String.format(
				"data.%s", DATA_KEY), DATA_VALUE)), is(true));
	}
}
