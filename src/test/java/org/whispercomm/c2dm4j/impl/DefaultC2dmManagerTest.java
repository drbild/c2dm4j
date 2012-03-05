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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.auth.AuthToken;
import org.whispercomm.c2dm4j.auth.TestableAuthTokenProvider;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
public class DefaultC2dmManagerTest {
	private static final String AUTH_TOKEN = "my auth token";

	private Message message;

	private TestableAuthTokenProvider factory;

	private HttpClient client;

	private Response response;

	private DefaultC2dmManager cut;

	private ArgumentCaptor<C2dmHttpPost> post = ArgumentCaptor
			.forClass(C2dmHttpPost.class);

	private ArgumentCaptor<C2dmHttpResponseHandler> handler = ArgumentCaptor
			.forClass(C2dmHttpResponseHandler.class);

	@Before
	public void setup() throws ClientProtocolException, IOException {
		message = new MessageBuilder().collapseKey("collapsekey")
				.registrationId("myregistrationid").put("mykey", "mydata")
				.build();
		factory = new TestableAuthTokenProvider(AUTH_TOKEN);
		client = mock(HttpClient.class);

		cut = new DefaultC2dmManager(client, factory);
	}

	@Test
	public void pushMessageSendsMessageAndReturnsResponse()
			throws ClientProtocolException, IOException {
		factory.resetWriteCount();
		response = new ResponseImpl(ResponseType.Success, message);
		when(
				client.execute(any(C2dmHttpPost.class),
						any(C2dmHttpResponseHandler.class))).thenReturn(
				response);

		Response ret = cut.pushMessage(message);

		verify(client).execute(post.capture(), handler.capture());

		assertThat("URI", post.getValue().getURI().toString(),
				is("https://android.apis.google.com/c2dm/send"));
		assertThat("AuthToken", post.getValue().getFirstHeader("Authorization")
				.getValue(), is(String.format("GoogleLogin auth=%s", factory
				.getAuthToken().toString())));

		assertThat("Message", handler.getValue().message, is(message));

		assertThat(factory.getWriteCount(), is(0));
		assertThat("Response", ret, is(response));
	}

	@Test
	public void pushMessageUpdatesAuthToken() throws ClientProtocolException,
			IOException {
		factory.resetWriteCount();
		AuthToken newToken = new AuthToken("my new auth token");
		response = new ResponseImpl(ResponseType.Success, message, newToken);
		when(
				client.execute(any(C2dmHttpPost.class),
						any(C2dmHttpResponseHandler.class))).thenReturn(
				response);

		cut.pushMessage(message);

		assertThat(factory.getWriteCount(), is(1));
		assertThat(factory.getAuthToken(), is(newToken));
	}
}
