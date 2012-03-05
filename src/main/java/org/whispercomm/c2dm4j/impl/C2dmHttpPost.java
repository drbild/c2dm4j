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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * Encapsulates an HTTP POST request to the C2DM service. This class is
 * responsible for constructing the request from a {@link Message} instance.
 * 
 * @author David R. Bild
 * 
 */
class C2dmHttpPost extends HttpPost {

	private static final String REGISTRATION_ID = "registration_id";
	private static final String COLLAPSE_ID = "collapse_key";
	private static final String DELAY_WHILE_IDLE = "delay_while_idle";
	private static final String DATA_KEY_PREFIX = "data.";

	/**
	 * Constructs a new POST requests for the specified message, authentication
	 * token, and endpoint.
	 * 
	 * @param message
	 *            the message to be placed into the request body
	 * @param token
	 *            the authentication token for the request
	 * @param uri
	 *            the remote endpoint for the request
	 */
	public C2dmHttpPost(Message message, AuthToken token, URI uri) {
		super(uri);
		initAuthToken(token);
		initPostEntity(message);
	}

	private void initAuthToken(AuthToken token) {
		this.setHeader("Authorization",
				String.format("GoogleLogin auth=%s", token));
	}

	private void initPostEntity(Message message) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		addParam(params, REGISTRATION_ID, message.getRegistrationId());
		addParam(params, COLLAPSE_ID, message.getCollapseKey());
		if (message.delayWhileIdle())
			addParam(params, DELAY_WHILE_IDLE);

		Map<String, String> data = message.getData();
		for (String key : data.keySet()) {
			addParam(params, DATA_KEY_PREFIX + key, data.get(key));
		}

		try {
			this.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			/*
			 * This should not be a checked exception. Good testing will catch
			 * if an unsupported encoding is requested.
			 */
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static void addParam(List<NameValuePair> params, String key,
			String value) {
		params.add(new BasicNameValuePair(key, value));
	}

	private static void addParam(List<NameValuePair> params, String key) {
		params.add(new BasicNameValuePair(key, null));
	}
}
