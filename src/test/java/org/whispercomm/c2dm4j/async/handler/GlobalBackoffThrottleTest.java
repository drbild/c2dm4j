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
package org.whispercomm.c2dm4j.async.handler;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.TestMessageFactory;
import org.whispercomm.c2dm4j.UnavailableResponse;
import org.whispercomm.c2dm4j.async.TestContextFactory;
import org.whispercomm.c2dm4j.backoff.Attempt;
import org.whispercomm.c2dm4j.backoff.TestableBackoff;
import org.whispercomm.c2dm4j.impl.TestResponseFactory;

import static org.whispercomm.c2dm4j.test.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalBackoffThrottle}.
 * 
 * @author David R. Bild
 * 
 */
public class GlobalBackoffThrottleTest {

	private TestableBackoff backoff;

	private GlobalBackoffThrottle cut;

	@Before
	public void setup() {
		backoff = new TestableBackoff();
		cut = new GlobalBackoffThrottle(backoff);
	}

	@Test
	public void constructorRegistersWithAsyncHandlers() {
		AsyncHandlers handlers = mock(AsyncHandlers.class);
		cut = new GlobalBackoffThrottle(backoff, handlers);

		verify(handlers).appendEnqueueFilter(cut);
		verify(handlers).appendDequeueFilter(cut);
		verify(handlers).appendResponseHandler(ResponseType.QuotaExceeded, cut);
		verify(handlers).appendResponseHandler(ResponseType.ServiceUnavailable,
				cut);
		verify(handlers).appendResponseHandler(ResponseType.Success, cut);
		verifyNoMoreInteractions(handlers);
	}

	@Test
	public void enqueueFilterUpdatesDelayIfBackoffGreaterThanExisting() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		backoff.setDelay(100L);

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(100L));
	}

	@Test
	public void enqueueFilterDoesNotUpdateDelayIfBackoffLesserThanExisting() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDelay(200L);
		backoff.setDelay(100L);

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(200L));
	}

	@Test
	public void enqueueFilterDoesNothingIfCancelled() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDecision(MessageDecision.CANCEL);
		backoff.setDelay(100L);

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.CANCEL));
		assertThat(context.getDelay(), is(0L));
	}

	@Test
	public void enqueueFilterAddsAttemptToContext() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		backoff.setDelay(100L);

		cut.enqueueFilter(context);
		assertThat(
				context.get(GlobalBackoffThrottle.CONTEXT_KEY, Attempt.class),
				is(notNullValue()));
	}

	@Test
	public void dequeueFilterUpdatesDelayIfBackoffGreaterThanExisting() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		backoff.setDelay(100L);

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(100L));
	}

	@Test
	public void dequeueFilterDoesNotUpdateDelayIfBackoffLesserThanExisting() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDelay(200L);
		backoff.setDelay(100L);

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(200L));
	}

	@Test
	public void dequeueFilterDoesNothingIfCancelled() {
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDecision(MessageDecision.CANCEL);
		backoff.setDelay(100L);

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.CANCEL));
		assertThat(context.getDelay(), is(0L));
	}

	@Test
	public void recordsSuccess() {
		Context<Response, ResultDecision> context = TestContextFactory
				.createResponseContext(TestResponseFactory.createSuccess());
		context.put(GlobalBackoffThrottle.CONTEXT_KEY, backoff.begin());

		cut.handleResponse(context);
		assertThat(backoff.successRecorded(), is(true));
		assertThat(context.getDecision(), is(ResultDecision.RETURN));
	}

	@Test
	public void recordsFailureForServiceUnavailable() {
		Context<Response, ResultDecision> context = TestContextFactory
				.createResponseContext(TestResponseFactory.createUnavailable());
		context.put(GlobalBackoffThrottle.CONTEXT_KEY, backoff.begin());

		cut.handleResponse(context);
		assertThat(backoff.failureRecorded(), is(true));
		assertThat(context.getDecision(), is(ResultDecision.RETRY));
	}

	@Test
	public void recordsFailureForQuotaExceeded() {
		Context<Response, ResultDecision> context = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.QuotaExceeded, TestMessageFactory.create()));
		context.put(GlobalBackoffThrottle.CONTEXT_KEY, backoff.begin());

		cut.handleResponse(context);
		assertThat(backoff.failureRecorded(), is(true));
		assertThat(context.getDecision(), is(ResultDecision.RETRY));
	}

	@Test
	public void retryHeaderIsParsedAndUsed() {
		// Input unavailable response with retry-after header
		UnavailableResponse response = TestResponseFactory.createUnavailable(
				new Date(System.currentTimeMillis() + 10000),
				TestMessageFactory.create());
		Context<Response, ResultDecision> responseContext = TestContextFactory
				.createResponseContext(response);
		responseContext.put(GlobalBackoffThrottle.CONTEXT_KEY, backoff.begin());
		cut.handleResponse(responseContext);

		// Queue up a new message, ensuring the retry-after header is obeyed
		Context<Message, MessageDecision> messageContext = TestContextFactory
				.createMessageContext();
		cut.enqueueFilter(messageContext);
		assertThat(messageContext.getDelay(), is(approx(10000L, 50)));
	}
}
