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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispercomm.c2dm4j.C2dmManager;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.UnexpectedResponseException;
import org.whispercomm.c2dm4j.auth.AuthTokenProvider;

/**
 * The default implementation of {@link C2dmManager} for pushing messages to
 * clients via the C2DM service. The instance is configured via the constructor,
 * which takes an {@link AuthTokenProvider} for retrieving and persisting
 * authentication tokens and an {@link HttpClient} used to the send HTTP
 * requests to the C2DM servers.
 * <p>
 * This class is thread-safe only if the provided {@link HttpClient} instance is
 * thread-safe. An instance which is safe up to {@code MAX_THREADS} concurrent
 * threads (i.e., sufficient for a thread-pool of size {@code MAX_THREADS}) can
 * be obtained like this:<br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp; ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; conmManager.setMaxTotal(MAX_THREADS); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; connManager.setDefaultMaxPerRoute(MAX_THREADS); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; HttpClient client = new DefaultHttpClient(connManager); <br/>
 * </code>
 * <p>
 * SLF4J is used for logging.
 * 
 * @author David R. Bild
 * 
 */
public class DefaultC2dmManager implements C2dmManager {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultC2dmManager.class);

	private static final String URI_STRING = "https://android.apis.google.com/c2dm/send";
	private static final URI URI;
	static {
		try {
			URI = new URI(URI_STRING);
		} catch (URISyntaxException e) {
			LOGGER.debug("Failed to create URI.", e);
			throw new RuntimeException(String.format(
					"Default URI is invalid: %s", URI_STRING), e);

		}
	}

	private final AuthTokenProvider tokenProvider;

	private final HttpClient httpClient;

	/**
	 * Constructs a new DefaultC2dmManager using the specified
	 * {@code HttpClient} to send HTTP requests and {@code AuthTokenProvider} to
	 * retrieve and persist the authentication token. The instance is
	 * thread-safe only if the {@code HttpClient} instance is. See
	 * {@link DefaultC2dmManager} for details.
	 * 
	 * @param httpClient
	 *            the client used to send HTTP requests
	 * 
	 * @param tokenProvider
	 *            the token provider used to retrieve and persist authentication
	 *            tokens
	 */
	public DefaultC2dmManager(HttpClient httpClient,
			AuthTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Response pushMessage(Message msg) throws IOException,
			UnexpectedResponseException {
		LOGGER.debug("Sending C2DM message: {}", msg);
		Response response = httpClient.execute(new C2dmHttpPost(msg,
				tokenProvider.getAuthToken(), URI),
				new C2dmHttpResponseHandler(msg));
		if (response.hasUpdatedAuthToken())
			tokenProvider.updateAuthToken(response.getUpdatedAuthToken());
		LOGGER.debug("Received C2DM reponse: {}", response);
		return response;
	}

}
