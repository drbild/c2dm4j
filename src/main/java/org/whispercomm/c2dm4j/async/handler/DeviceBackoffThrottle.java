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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.backoff.Attempt;
import org.whispercomm.c2dm4j.backoff.Backoff;
import org.whispercomm.c2dm4j.backoff.BackoffProvider;

/**
 * A message filter and response handler that implements per-device retry with
 * backoff for {@link ResponseType#DeviceQuotaExceeded
 * DeviceQuotaExceeded} responses.
 * 
 * @author David R. Bild
 * 
 */
public class DeviceBackoffThrottle implements MessageFilter,
		ResponseHandler<Response> {
	static final String CONTEXT_KEY = DeviceBackoffThrottle.class
			.getCanonicalName();

	private final BackoffProvider provider;

	private ConcurrentMap<String, Backoff> backoffs;

	/**
	 * Constructs a new throttle that will use the specified provider to
	 * construct {@link Backoff} instances as needed.
	 * 
	 * @param provider
	 *            the provider to construct backoff objects as needed
	 */
	public DeviceBackoffThrottle(BackoffProvider provider) {
		this.provider = provider;
		this.backoffs = new ConcurrentHashMap<String, Backoff>();
	}

	/**
	 * Constructs a new throttle that will use the specified provider to
	 * construct {@link Backoff} instances as needed and registers the
	 * underlying filters and handlers with the provider {@link AsyncHandlers}
	 * instance.
	 * 
	 * @param provider
	 *            the provider to construct backoff objects as needed
	 * @param handlers
	 *            the handlers object with which to register the filters and
	 *            handlers
	 */
	public DeviceBackoffThrottle(BackoffProvider provider,
			AsyncHandlers handlers) {
		this(provider);
		handlers.appendEnqueueFilter(this);
		handlers.appendDequeueFilter(this);
		handlers.appendResponseHandler(ResponseType.DeviceQuotaExceeded, this);
		handlers.appendResponseHandler(ResponseType.Success, this);
		handlers.appendResponseHandler(ResponseType.InvalidRegistration, this);
		handlers.appendResponseHandler(ResponseType.NotRegistered, this);
	}

	// ------------------------- Filter Messages ------------------------------
	@Override
	public void enqueueFilter(Context<Message, MessageDecision> context) {
		String deviceKey = context.unwrap().getRegistrationId();
		updateDelay(context, deviceKey);
	}

	@Override
	public void dequeueFilter(Context<Message, MessageDecision> context) {
		String deviceKey = context.unwrap().getRegistrationId();
		updateDelay(context, deviceKey);
	}

	private Backoff retrieveBackoff(String deviceKey) {
		return backoffs.get(deviceKey);
	}

	private Attempt createAttempt(Context<Message, MessageDecision> context,
			String deviceKey) {
		Backoff backoff = retrieveBackoff(deviceKey);
		if (backoff != null) {
			Attempt attempt = backoff.begin();
			context.put(CONTEXT_KEY, attempt);
			return attempt;
		} else {
			return null;
		}
	}

	private void updateDelay(Context<Message, MessageDecision> context,
			String deviceKey) {
		switch (context.getDecision()) {
		case SEND:
			Attempt attempt = createAttempt(context, deviceKey);
			if (attempt != null) {
				long delay = attempt.delay();
				if (delay > context.getDelay())
					context.setDelay(delay);
			}
			return;
		default:
			return;
		}
	}

	// ------------------------- Handle Responses -----------------------------
	@Override
	public void handleResponse(Context<Response, ResultDecision> context) {
		Response response = context.unwrap();
		String deviceKey = response.getMessage().getRegistrationId();
		switch (response.getResponseType()) {
		case DeviceQuotaExceeded:
			context.setDecision(ResultDecision.RETRY);
			recordFailure(context, deviceKey);
			return;
		case Success:
		case InvalidRegistration:
		case NotRegistered:
			removeBackoff(deviceKey);
		default:
			return;
		}
	}

	private void recordFailure(Context<Response, ResultDecision> context,
			String deviceKey) {
		Attempt attempt = retrieveAttempt(context);
		if (attempt != null) {
			attempt.recordFailure();
		} else {
			createOrRetrieveBackoff(deviceKey).begin().recordFailure();
		}
	}

	private Attempt retrieveAttempt(Context<Response, ResultDecision> context) {
		return context.get(CONTEXT_KEY, Attempt.class);
	}

	private Backoff createOrRetrieveBackoff(String deviceKey) {
		Backoff backoff = retrieveBackoff(deviceKey);
		if (backoff == null) {
			Backoff newBackoff = provider.createBackoff();
			backoff = backoffs.putIfAbsent(deviceKey, newBackoff);
			if (backoff == null)
				backoff = newBackoff;
		}
		return backoff;
	}

	private void removeBackoff(String deviceKey) {
		backoffs.remove(deviceKey);
	}

}
