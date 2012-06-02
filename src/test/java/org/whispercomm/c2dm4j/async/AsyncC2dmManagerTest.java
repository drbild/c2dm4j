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
package org.whispercomm.c2dm4j.async;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlers;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlersFactory;
import org.whispercomm.c2dm4j.async.handler.GlobalBackoffThrottle;
import org.whispercomm.c2dm4j.backoff.ExponentialBackoff;
import org.whispercomm.c2dm4j.impl.MockC2dmManager;

/**
 * Tests for (@link AsyncC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
public class AsyncC2dmManagerTest {

	private ScheduledExecutorService executor;

	private MockC2dmManager manager;

	private AsyncHandlers handlers;

	private AsyncC2dmManager cut;

	private Message msg;

	@Before
	public void setup() {
		executor = new ScheduledThreadPoolExecutor(1);
		manager = new MockC2dmManager();
		handlers = AsyncHandlersFactory.createEmpty();
		cut = new AsyncC2dmManagerImpl(manager, handlers, executor);

		msg = new MessageBuilder().collapseKey("collapsekey")
				.registrationId("myregistrationid").put("mykey", "mydata")
				.build();
	}

	@Test(timeout = 1000)
	public void testSuccessfulSend() throws InterruptedException,
			ExecutionException {
		manager.enqueue(ResponseType.Success);
		Future<Response> fut = cut.pushMessage(msg);
		assertThat(fut.get().getResponseType(), is(ResponseType.Success));
	}

	@Test(timeout = 1000)
	public void testRetriesOnFailure() throws InterruptedException,
			ExecutionException {
		new GlobalBackoffThrottle(new ExponentialBackoff(), handlers);

		manager.enqueue(ResponseType.QuotaExceeded);
		manager.enqueue(ResponseType.QuotaExceeded);
		manager.enqueue(ResponseType.QuotaExceeded);
		manager.enqueue(ResponseType.QuotaExceeded);
		manager.enqueue(ResponseType.Success);

		Future<Response> fut = cut.pushMessage(msg);
		assertThat(fut.get().getResponseType(), is(ResponseType.Success));
	}
}
