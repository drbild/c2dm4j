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
package org.whispercomm.c2dm4j.async.handler;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.TestMessageFactory;
import org.whispercomm.c2dm4j.async.TestContextFactory;
import org.whispercomm.c2dm4j.backoff.Attempt;
import org.whispercomm.c2dm4j.backoff.TestableBackoff;
import org.whispercomm.c2dm4j.impl.TestResponseFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for (@link DeviceBackoffThrottle}.
 * 
 * @author David R. Bild
 * 
 */
public class DeviceBackoffThrottleTest {

	private TestableBackoff.Provider provider;

	private DeviceBackoffThrottle cut;

	@Before
	public void setup() {
		provider = new TestableBackoff.Provider();
		cut = new DeviceBackoffThrottle(provider);
	}

	@Test
	public void constructorRegistersWithAsyncHandlers() {
		AsyncHandlers handlers = mock(AsyncHandlers.class);
		cut = new DeviceBackoffThrottle(provider, handlers);

		verify(handlers).appendEnqueueFilter(cut);
		verify(handlers).appendDequeueFilter(cut);
		verify(handlers).appendResponseHandler(
				ResponseType.DeviceQuotaExceeded, cut);
		verify(handlers).appendResponseHandler(ResponseType.NotRegistered, cut);
		verify(handlers).appendResponseHandler(
				ResponseType.InvalidRegistration, cut);
		verify(handlers).appendResponseHandler(ResponseType.Success, cut);
		verifyNoMoreInteractions(handlers);
	}

	/**
	 * Issue a failed response, so a backoff object for the device is created.
	 */
	private void prime() {
		Context<Response, ResultDecision> responseContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.DeviceQuotaExceeded,
						TestMessageFactory.create()));
		cut.handleResponse(responseContext);
	}

	@Test
	public void enqueueFilterUpdatesDelayIfBackoffGreaterThanExisting() {
		provider.setDelay(100L);
		prime();
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(100L));
	}

	@Test
	public void enqueueFilterDoesNotUpdateDelayIfBackoffLesserThanExisting() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDelay(200L);

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(200L));
	}

	@Test
	public void enqueueFilterDoesNothingIfCancelled() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDecision(MessageDecision.CANCEL);

		cut.enqueueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.CANCEL));
		assertThat(context.getDelay(), is(0L));
	}

	@Test
	public void enqueueFilterAddsAttemptToContext() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();

		cut.enqueueFilter(context);
		assertThat(
				context.get(DeviceBackoffThrottle.CONTEXT_KEY, Attempt.class),
				is(notNullValue()));
	}

	@Test
	public void dequeueFilterUpdatesDelayIfBackoffGreaterThanExisting() {
		provider.setDelay(100L);
		prime();
		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(100L));
	}

	@Test
	public void dequeueFilterDoesNotUpdateDelayIfBackoffLesserThanExisting() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDelay(200L);

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.SEND));
		assertThat(context.getDelay(), is(200L));
	}

	@Test
	public void dequeueFilterDoesNothingIfCancelled() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();
		context.setDecision(MessageDecision.CANCEL);

		cut.dequeueFilter(context);
		assertThat(context.getDecision(), is(MessageDecision.CANCEL));
		assertThat(context.getDelay(), is(0L));
	}

	@Test
	public void dequeueFilterAddsAttemptToContext() {
		provider.setDelay(100L);
		prime();

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();

		cut.dequeueFilter(context);
		assertThat(
				context.get(DeviceBackoffThrottle.CONTEXT_KEY, Attempt.class),
				is(notNullValue()));
	}

	@Test
	public void removesBackoffForSuccess() {
		prime();
		provider.mostRecent().begin().recordFailure();
		Context<Response, ResultDecision> responseContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createSuccess());
		responseContext.put(DeviceBackoffThrottle.CONTEXT_KEY, provider
				.mostRecent().begin());

		cut.handleResponse(responseContext);

		Context<Message, MessageDecision> messageContext = TestContextFactory
				.createMessageContext();

		cut.dequeueFilter(messageContext);
		assertThat(messageContext.getDecision(), is(MessageDecision.SEND));
		assertThat(messageContext.getDelay(), is(0L));
	}

	@Test
	public void removesBackoffForInvalidRegistration() {
		prime();
		provider.mostRecent().begin().recordFailure();
		Context<Response, ResultDecision> responseContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.InvalidRegistration,
						TestMessageFactory.create()));
		responseContext.put(DeviceBackoffThrottle.CONTEXT_KEY, provider
				.mostRecent().begin());

		cut.handleResponse(responseContext);

		Context<Message, MessageDecision> messageContext = TestContextFactory
				.createMessageContext();

		cut.dequeueFilter(messageContext);
		assertThat(messageContext.getDecision(), is(MessageDecision.SEND));
		assertThat(messageContext.getDelay(), is(0L));
	}

	@Test
	public void removesBackoffForNotRegistered() {
		prime();
		provider.mostRecent().begin().recordFailure();
		Context<Response, ResultDecision> responseContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.NotRegistered, TestMessageFactory.create()));
		responseContext.put(DeviceBackoffThrottle.CONTEXT_KEY, provider
				.mostRecent().begin());

		cut.handleResponse(responseContext);

		Context<Message, MessageDecision> messageContext = TestContextFactory
				.createMessageContext();

		cut.dequeueFilter(messageContext);
		assertThat(messageContext.getDecision(), is(MessageDecision.SEND));
		assertThat(messageContext.getDelay(), is(0L));
	}

	@Test
	public void recordsFailureForDeviceQuotaExceeded() {
		prime();
		Context<Response, ResultDecision> context = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.DeviceQuotaExceeded,
						TestMessageFactory.create()));
		context.put(GlobalBackoffThrottle.CONTEXT_KEY, provider.mostRecent()
				.begin());

		cut.handleResponse(context);
		assertThat(provider.mostRecent().failureRecorded(), is(true));
		assertThat(context.getDecision(), is(ResultDecision.RETRY));
	}

}
