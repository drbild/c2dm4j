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
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.UnexpectedResponseException;
import org.whispercomm.c2dm4j.auth.AuthToken;

/**
 * A handler responsible for parsing C2DM http responses to construct
 * {@link ResponseImpl}, {@link SuccesssResponseImpl}, and
 * {@link UnavailableResponseImpl} objects encapsulating them.
 * 
 * @author David R. Bild
 * 
 */
class C2dmHttpResponseHandler implements ResponseHandler<Response> {
	private static final String CLIENT_AUTH_HEADER = "Update-Client-Auth";

	private static final Pattern SPLITTER = Pattern.compile("=");

	final Message message;

	public C2dmHttpResponseHandler(Message message) {
		this.message = message;
	}

	@Override
	public Response handleResponse(HttpResponse response) throws IOException {
		AuthToken token = getAuthToken(response);

		switch (response.getStatusLine().getStatusCode()) {
		case 200:
			NameValuePair body = parseBody(response);
			ResponseType type = getResponseType(body);
			switch (type) {
			case Success:
				return new SuccessResponseImpl(body.getValue(), message, token);
			default:
				return new ResponseImpl(type, message, token);
			}
		case 503:
			Date retryAfter = getRetryAfter(response);
			return new UnavailableResponseImpl(retryAfter, message, token);
		case 401:
			return new ResponseImpl(ResponseType.Unauthorized, message, token);
		default:
			throw new UnexpectedResponseException(String.format(
					"Unexpected HTTP status code: %d", response.getStatusLine()
							.getStatusCode()));
		}
	}

	private AuthToken getAuthToken(HttpResponse response) {
		Header authHeader = response.getFirstHeader(CLIENT_AUTH_HEADER);
		if (authHeader != null)
			return new AuthToken(authHeader.getValue());
		else
			return null;
	}

	private Date getRetryAfter(HttpResponse response) {
		Header retryAfterHeader = response.getFirstHeader("Retry-After");
		if (retryAfterHeader != null) {
			// Read as HTTP-Date
			try {
				return org.apache.http.impl.cookie.DateUtils
						.parseDate(retryAfterHeader.getValue());
			} catch (DateParseException e) {
			}

			// Read as seconds
			try {
				return new Date(System.currentTimeMillis() + 1000L
						* Integer.valueOf(retryAfterHeader.getValue()));
			} catch (NumberFormatException e) {
			}
		}

		// Otherwise
		return null;
	}

	private NameValuePair parseBody(HttpResponse response)
			throws UnexpectedResponseException {
		try {
			String body = EntityUtils.toString(response.getEntity());
			String[] splitBody = SPLITTER.split(body);
			if (splitBody.length == 2) {
				return new BasicNameValuePair(splitBody[0], splitBody[1]);
			} else {
				throw new UnexpectedResponseException(String.format(
						"Unexpected format of message body:\n%s", body));
			}
		} catch (ParseException e) {
			throw new UnexpectedResponseException(e);
		} catch (IOException e) {
			throw new UnexpectedResponseException(e);
		}
	}

	private ResponseType getResponseType(NameValuePair body)
			throws UnexpectedResponseException {
		try {
			switch (ResponseKeys.valueOf(body.getName())) {
			case id:
				return ResponseType.Success;
			case Error:
				switch (ResponseErrorValues.valueOf(body.getValue())) {
				case QuotaExceeded:
					return ResponseType.QuotaExceeded;
				case DeviceQuotaExceeded:
					return ResponseType.DeviceQuotaExceeded;
				case MissingRegistration:
					return ResponseType.MissingRegistration;
				case InvalidRegistration:
					return ResponseType.InvalidRegistration;
				case MismatchSenderId:
					return ResponseType.MismatchSenderId;
				case NotRegistered:
					return ResponseType.NotRegistered;
				case MessageTooBig:
					return ResponseType.MessageTooBig;
				case MissingCollapseKey:
					return ResponseType.MissingCollapseKey;
				default:
					throw new UnexpectedResponseException(
							"Unexpected error message.");
				}
			default:
				throw new UnexpectedResponseException(
						"Unexpected key in body name-value pair.");
			}
		} catch (IllegalArgumentException e) {
			throw new UnexpectedResponseException(
					"Unexpected format in message.");
		}
	}

	/**
	 * Keys used in the {@code 200} responses from the C2DM service.
	 * 
	 * @author David R. Bild
	 * 
	 */
	static enum ResponseKeys {
		id, Error
	}

	/**
	 * Possible values for the {@code Error} key in {@code 200} responses from
	 * the C2DM service.
	 * 
	 * @author David R. Bild
	 * 
	 */
	static enum ResponseErrorValues {
		QuotaExceeded, DeviceQuotaExceeded, MissingRegistration, InvalidRegistration, MismatchSenderId, NotRegistered, MessageTooBig, MissingCollapseKey
	}
}
