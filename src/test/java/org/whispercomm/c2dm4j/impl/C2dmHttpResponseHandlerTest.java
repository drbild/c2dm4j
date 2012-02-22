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
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.SuccessResponse;
import org.whispercomm.c2dm4j.UnavailableResponse;
import org.whispercomm.c2dm4j.UnexpectedResponseException;
import org.whispercomm.c2dm4j.auth.AuthToken;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link C2dmHttpResponseHandler}.
 * 
 * @author David R. Bild
 * 
 */
public class C2dmHttpResponseHandlerTest {
	private static final String SENT_MESSAGE_ID = "0123456789abcedf";

	private Message message;

	private C2dmHttpResponseHandler cut;

	@Before
	public void setup() {
		message = new MessageBuilder().collapseKey("My collapse key")
				.registrationId("My C2DM registration id").delayWhileIdle(true)
				.put("mykey", "myvalue").build();
		cut = new C2dmHttpResponseHandler(message);
	}

	private HttpResponse buildResponse(int statusCode) {
		return new BasicHttpResponse(HttpVersion.HTTP_1_1, statusCode,
				EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode,
						Locale.US));
	}

	@Test(expected = UnexpectedResponseException.class)
	public void throwsUnexpectedResponseExceptionForMalformattedSuccessEntity()
			throws IOException {
		HttpResponse response = buildResponse(200);
		response.setEntity(new StringEntity("id=1234;id=1234"));

		cut.handleResponse(response);
	}

	@Test(expected = UnexpectedResponseException.class)
	public void throwsUnexpectedResponseExceptionForInvalidSuccessEntityKey()
			throws IOException {
		HttpResponse response = buildResponse(200);
		response.setEntity(new StringEntity("badkey=0122345678910"));

		cut.handleResponse(response);
	}

	private HttpResponse buildSuccessResponse()
			throws UnsupportedEncodingException {
		HttpResponse response = buildResponse(200);
		response.setEntity(new StringEntity(String.format("id=%s",
				SENT_MESSAGE_ID)));
		return response;
	}

	@Test
	public void parsesSuccess() throws IOException {
		HttpResponse response = buildSuccessResponse();

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(), is(ResponseType.Success));
		assertThat(result.getMessage(), is(message));
		assertThat((result instanceof SuccessResponse), is(true));

		SuccessResponse sResult = (SuccessResponse) result;
		assertThat(sResult.getSentMessageId(), is(SENT_MESSAGE_ID));
	}

	private HttpResponse buildErrorResponse(String error)
			throws UnsupportedEncodingException {
		HttpResponse response = buildResponse(200);
		response.setEntity(new StringEntity(String.format("Error=%s", error)));
		return response;
	}

	private void parsesError(String error, ResponseType expected)
			throws IOException {
		HttpResponse response = buildErrorResponse(error);

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(), is(expected));
		assertThat(result.getMessage(), is(message));
	}

	@Test
	public void parsesQuotaExceeded() throws IOException {
		parsesError("QuotaExceeded", ResponseType.QuotaExceeded);
	}

	@Test
	public void parsesDeviceQuotaExceeded() throws IOException {
		parsesError("DeviceQuotaExceeded", ResponseType.DeviceQuotaExceeded);
	}

	@Test
	public void parsesMissingRegistration() throws IOException {
		parsesError("MissingRegistration", ResponseType.MissingRegistration);
	}

	@Test
	public void parsesInvalidRegistration() throws IOException {
		parsesError("InvalidRegistration", ResponseType.InvalidRegistration);
	}

	@Test
	public void parsesMismatchSenderId() throws IOException {
		parsesError("MismatchSenderId", ResponseType.MismatchSenderId);
	}

	@Test
	public void parsesNotRegistered() throws IOException {
		parsesError("NotRegistered", ResponseType.NotRegistered);
	}

	@Test
	public void parsesMessageTooBig() throws IOException {
		parsesError("MessageTooBig", ResponseType.MessageTooBig);
	}

	@Test
	public void parsesMissingCollapseKey() throws IOException {
		parsesError("MissingCollapseKey", ResponseType.MissingCollapseKey);
	}

	@Test(expected = UnexpectedResponseException.class)
	public void throwsUnexpectedResponseExceptionForUnknownErrorValue()
			throws IOException {
		parsesError("not_a_real_error_code", null);
	}

	@Test
	public void parsesUnauthorized() throws IOException {
		HttpResponse response = buildResponse(401);

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(), is(ResponseType.Unauthorized));
		assertThat(result.getMessage(), is(message));
	}

	@Test
	public void parsesUnavailableWithoutRetryHeader() throws IOException {
		HttpResponse response = buildResponse(503);

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(),
				is(ResponseType.ServiceUnavailable));
		assertThat(result.getMessage(), is(message));
		assertThat((result instanceof UnavailableResponse), is(true));

		UnavailableResponse uResult = (UnavailableResponse) result;
		assertThat(uResult.hasRetryAfter(), is(false));
		assertThat(uResult.retryAfter(), is(nullValue()));
	}

	@Test
	public void parsesUnavailableWithRetryAsDate() throws IOException {
		HttpResponse response = buildResponse(503);

		Date retryAfter = new Date(1000000000000L);
		response.setHeader("Retry-After", "Sun, 09 Sep 2001 01:46:40 GMT");

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(),
				is(ResponseType.ServiceUnavailable));
		assertThat(result.getMessage(), is(message));
		assertThat((result instanceof UnavailableResponse), is(true));

		UnavailableResponse uResult = (UnavailableResponse) result;
		assertThat(uResult.hasRetryAfter(), is(true));
		assertThat(uResult.retryAfter(), is(retryAfter));
	}

	@Test
	public void parsesUnavailableWithRetryAsSeconds() throws IOException {
		HttpResponse response = buildResponse(503);

		DateTime expected = new DateTime().plusSeconds(42);
		response.setHeader("Retry-After", "42");

		Response result = cut.handleResponse(response);
		assertThat(result.getResponseType(),
				is(ResponseType.ServiceUnavailable));
		assertThat(result.getMessage(), is(message));
		assertThat((result instanceof UnavailableResponse), is(true));

		UnavailableResponse uResult = (UnavailableResponse) result;
		assertThat(uResult.hasRetryAfter(), is(true));
		DateTime retryAfter = new DateTime(uResult.retryAfter());
		// Check that the different between the expected and returned retry time
		// is less than one second.
		assertThat(new Duration(expected, retryAfter).isShorterThan(Duration
				.standardSeconds(1)), is(true));
	}

	@Test(expected = UnexpectedResponseException.class)
	public void throwsUnexpectedResponseForUnexpectedStatusCode()
			throws IOException {
		HttpResponse response = buildResponse(404);

		cut.handleResponse(response);
	}

	@Test
	public void retrievesAuthTokenHeader() throws IOException {
		HttpResponse response = buildSuccessResponse();

		AuthToken newAuthToken = new AuthToken("My new auth token");
		response.addHeader("Update-Client-Auth", newAuthToken.toString());

		Response result = cut.handleResponse(response);
		assertThat(result.hasUpdatedAuthToken(), is(true));
		assertThat(result.getUpdatedAuthToken(), is(newAuthToken));
	}

	@Test
	public void acceptsMissingAuthTokenHeader() throws IOException {
		HttpResponse response = buildSuccessResponse();

		Response result = cut.handleResponse(response);
		assertThat(result.hasUpdatedAuthToken(), is(false));
	}

}
