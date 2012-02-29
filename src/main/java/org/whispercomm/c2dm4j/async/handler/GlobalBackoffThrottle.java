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

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.UnavailableResponse;
import org.whispercomm.c2dm4j.backoff.Attempt;
import org.whispercomm.c2dm4j.backoff.Backoff;

/**
 * A message filter and response handler that implements global retry with
 * backoff for {@link ResponseType#QuotaExceeded QuotaExceeded} and
 * {@link ResponseType#ServiceUnavailable ServiceUnavailable} responses and
 * delays messages to respect {@code Retry-After} headers.
 * 
 * @author David R. Bild
 * 
 */
public class GlobalBackoffThrottle implements MessageFilter,
		ResponseHandler<Response> {
	static final String CONTEXT_KEY = GlobalBackoffThrottle.class
			.getCanonicalName();

	private Backoff backoff;

	private long nextRetryTime;

	/**
	 * Constructs a new throttle using the provided {@code Backoff} instance for
	 * backoff.
	 * 
	 * @param backoff
	 *            the backoff object
	 */
	public GlobalBackoffThrottle(Backoff backoff) {
		this.backoff = backoff;
	}

	/**
	 * Constructs a new throttle using the provided {@code Backoff} instance for
	 * backoff and registers the underlying filters and handlers with the
	 * provider {@link AsyncHandlers} instance.
	 * 
	 * @param backoff
	 *            the backoff object
	 * @param handlers
	 *            the handlers object with which to register the filters and
	 *            handlers
	 */
	public GlobalBackoffThrottle(Backoff backoff, AsyncHandlers handlers) {
		this(backoff);
		register(handlers);
	}

	private void register(AsyncHandlers handlers) {
		handlers.appendEnqueueFilter(this);
		handlers.appendDequeueFilter(this);
		handlers.appendResponseHandler(ResponseType.ServiceUnavailable, this);
		handlers.appendResponseHandler(ResponseType.QuotaExceeded, this);
		handlers.appendResponseHandler(ResponseType.Success, this);
	}

	// ------------------------- Filter Messages ------------------------------
	@Override
	public void enqueueFilter(Context<Message, MessageDecision> context) {
		updateDelay(context);
	}

	@Override
	public void dequeueFilter(Context<Message, MessageDecision> context) {
		updateDelay(context);
	}

	private Attempt createAttempt(Context<Message, MessageDecision> context) {
		Attempt attempt = backoff.begin();
		context.put(CONTEXT_KEY, attempt);
		return attempt;
	}

	private void updateDelay(Context<Message, MessageDecision> context) {
		switch (context.getDecision()) {
		case SEND:
			Attempt attempt = createAttempt(context);
			long delay = Math.max(attempt.delay(), retryDelay());
			if (delay > context.getDelay())
				context.setDelay(delay);
			return;
		default:
			return;
		}
	}

	private long retryDelay() {
		long delay = nextRetryTime - System.currentTimeMillis();
		return Math.max(0, delay);
	}

	// ------------------------- Handle Responses -----------------------------
	@Override
	public void handleResponse(Context<Response, ResultDecision> context) {
		Response response = context.unwrap();
		switch (response.getResponseType()) {
		case ServiceUnavailable:
			retrieveAttempt(context).recordFailure();
			updateRetryAfter((UnavailableResponse) response);
			context.setDecision(ResultDecision.RETRY);
			return;
		case QuotaExceeded:
			retrieveAttempt(context).recordFailure();
			context.setDecision(ResultDecision.RETRY);
			return;
		case Success:
			retrieveAttempt(context).recordSuccess();
			return;
		default:
			return;
		}
	}

	private Attempt retrieveAttempt(Context<Response, ResultDecision> context) {
		return context.get(CONTEXT_KEY, Attempt.class);
	}

	private void updateRetryAfter(UnavailableResponse response) {
		if (response.hasRetryAfter())
			nextRetryTime = response.retryAfter().getTime();
	}

}
