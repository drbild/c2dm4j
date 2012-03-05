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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.TestMessageFactory;
import org.whispercomm.c2dm4j.async.TestContextFactory;
import org.whispercomm.c2dm4j.impl.TestResponseFactory;

/**
 * Unit tests for {@link AsyncHandlersImpl}.
 * 
 * @author David R. Bild
 * 
 */
public class AsyncHandlersImplTest {

	private AsyncHandlers cut;

	@Before
	public void setup() {
		cut = new AsyncHandlersImpl();
	}

	@Test
	public void onlyRegisteredQueueMethodsCalled() {
		MessageFilter enqueue = mock(MessageFilter.class);
		MessageFilter dequeue = mock(MessageFilter.class);
		cut.appendEnqueueFilter(enqueue);
		cut.appendDequeueFilter(dequeue);

		Context<Message, MessageDecision> context = TestContextFactory
				.createMessageContext();

		cut.filterMessageEnqueue(context);
		verify(enqueue).enqueueFilter(context);

		cut.filterMessageDequeue(context);
		verify(dequeue).dequeueFilter(context);

		verify(enqueue, never()).dequeueFilter(context);
		verify(dequeue, never()).enqueueFilter(context);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void onlyMatchingResponseTypeHandlersCalled() {
		ResponseHandler<Response> handlerSuccess = mock(ResponseHandler.class);
		ResponseHandler<Response> handlerUnavailable = mock(ResponseHandler.class);
		cut.appendResponseHandler(ResponseType.Success, handlerSuccess);
		cut.appendResponseHandler(ResponseType.ServiceUnavailable,
				handlerUnavailable);

		Context<Response, ResultDecision> successContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.Success, TestMessageFactory.create()));
		Context<Response, ResultDecision> unavailableContext = TestContextFactory
				.createResponseContext(TestResponseFactory.createResponse(
						ResponseType.ServiceUnavailable,
						TestMessageFactory.create()));

		cut.handleResponse(successContext);
		verify(handlerSuccess).handleResponse(successContext);
		verify(handlerUnavailable, never()).handleResponse(successContext);

		cut.handleResponse(unavailableContext);
		verify(handlerUnavailable).handleResponse(unavailableContext);
		verify(handlerSuccess, never()).handleResponse(unavailableContext);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void onlyMatchingThrowableHandlersCalled() {
		ThrowableHandler<Throwable> handlerException = mock(ThrowableHandler.class);
		ThrowableHandler<Throwable> handlerRuntime = mock(ThrowableHandler.class);
		cut.appendThrowableHandler(Exception.class, handlerException);
		cut.appendThrowableHandler(RuntimeException.class, handlerRuntime);

		Context<Throwable, ResultDecision> exceptionContext = TestContextFactory
				.createThrowableContext(new Exception("exception"));
		Context<Throwable, ResultDecision> runtimeContext = TestContextFactory
				.createThrowableContext(new RuntimeException("runtimeexception"));

		cut.handleThrowable(exceptionContext);
		verify(handlerException).handleThrowable(exceptionContext);
		verify(handlerRuntime, never()).handleThrowable(exceptionContext);

		cut.handleThrowable(runtimeContext);
		verify(handlerRuntime).handleThrowable(runtimeContext);
		verify(handlerException, never()).handleThrowable(runtimeContext);
	}
}
