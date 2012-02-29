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
package org.whispercomm.c2dm4j.async;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.client.HttpClient;
import org.whispercomm.c2dm4j.C2dmManager;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlers;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlersFactory;
import org.whispercomm.c2dm4j.auth.AuthTokenProvider;
import org.whispercomm.c2dm4j.impl.DefaultC2dmManager;

/**
 * Provides static methods for creating {@link AsyncC2dmManager} instances.
 * 
 * @author David R. Bild
 * 
 */
public class AsyncC2dmManagerFactory {

	/**
	 * Should not be instantiated.
	 */
	private AsyncC2dmManagerFactory() {
		throw new IllegalStateException();
	}

	/**
	 * Creates an {@code AsyncC2dmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via a
	 * {@link C2dmManager}. {@link AsyncHandlers} are used to filter the
	 * messages and automatically handle to responses and exceptions.
	 * <p>
	 * The given {@code C2dmManager} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently.
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: <code> </br>
	 * ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 * 
	 * @param manager
	 *            the synchronous manager for communicating with the C2DM
	 *            service
	 * @param handlers
	 *            the handlers for automatically handling responses and
	 *            exceptions
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncC2dmManager create(C2dmManager manager,
			AsyncHandlers handlers, ScheduledExecutorService executor) {
		return new AsyncC2dmManagerImpl(manager, handlers, executor);
	}

	/**
	 * Creates an {@code AsyncC2dmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via a
	 * {@link C2dmManager}.
	 * <p>
	 * A default {@link AsyncHandlers} instance that implements automatic retry
	 * with exponential back-off (globally for <code>Service Unavailable</code>
	 * and <code>Quota
	 * Exceeded</code> errors and per-device for
	 * <code>Device Quota Exceeded</code> errors) and honors
	 * <code>Retry-After</code> headers is registered.
	 * <p>
	 * The given {@code C2dmManager} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} might run concurrently.
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: <code> </br>
	 * ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 * 
	 * @param manager
	 *            the synchronous manager for communicating with the C2DM
	 *            service
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncC2dmManager create(C2dmManager manager,
			ScheduledExecutorService executor) {
		return create(manager, AsyncHandlersFactory.create(), executor);
	}

	/**
	 * Creates an {@code AsyncC2dmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via an
	 * {@link C2dmManager} instance constructed from the given
	 * {@link HttpClient} and {@link AuthTokenProvider}. {@link AsyncHandlers}
	 * are used to filter the messages and automatically handle to responses and
	 * exceptions.
	 * <p>
	 * The given {@code HttpClient} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently. An
	 * {@code HttpClient} instance for {@code MAX_THREADS} concurrent threads
	 * can be created like this: </br> <code> 
	 *   ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); </br>
	 *   conmManager.setMaxTotal(MAX_THREADS); </br>
	 *   connManager.setDefaultMaxPerRoute(MAX_THREADS); </br>
	 *   HttpClient client = new DefaultHttpClient(connManager); </br>
	 * </code>
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: </br> <code> 
	 *   ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 * 
	 * @param client
	 *            the client used to send HTTP requests
	 * @param provider
	 *            the provider used to retrieve and persist authentication
	 *            tokens
	 * @param handlers
	 *            the handlers for automatically handling responses and
	 *            exceptions
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncC2dmManager create(HttpClient client,
			AuthTokenProvider provider, AsyncHandlers handlers,
			ScheduledExecutorService executor) {
		return create(new DefaultC2dmManager(client, provider), handlers,
				executor);
	}

	/**
	 * Creates an {@code AsyncC2dmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via an
	 * {@link C2dmManager} instance constructed from the given
	 * {@link HttpClient} and {@link AuthTokenProvider}.
	 * <p>
	 * A default {@link AsyncHandlers} instance that implements automatic retry
	 * with exponential back-off (globally for <code>Service Unavailable</code>
	 * and <code>Quota
	 * Exceeded</code> errors and per-device for
	 * <code>Device Quota Exceeded</code> errors) and honors
	 * <code>Retry-After</code> headers is registered.
	 * <p>
	 * The given {@code HttpClient} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently. An
	 * {@code HttpClient} instance for {@code MAX_THREADS} concurrent threads
	 * can be created like this: </br> <code> 
	 *   ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); </br>
	 *   conmManager.setMaxTotal(MAX_THREADS); </br>
	 *   connManager.setDefaultMaxPerRoute(MAX_THREADS); </br>
	 *   HttpClient client = new DefaultHttpClient(connManager); </br>
	 * </code>
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: </br> <code> 
	 *   ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 * 
	 * @param client
	 *            the client used to send HTTP requests
	 * @param provider
	 *            the provider used to retrieve and persist authentication
	 *            tokens
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncC2dmManager create(HttpClient client,
			AuthTokenProvider provider, ScheduledExecutorService executor) {
		return create(client, provider, AsyncHandlersFactory.create(), executor);
	}

}
