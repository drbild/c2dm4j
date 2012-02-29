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

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.whispercomm.c2dm4j.Response;

/**
 * Future implementation for {@link Response} objects. This class does not
 * support canceling the underlying task, i.e., {@link #cancel(boolean)} always
 * returns {@code false}.
 * 
 * @author David R. Bild
 * 
 */
class FutureResponse implements Future<Response> {

	private final static int WAITING = 0;
	private final static int DONE = 1;
	private final static int CANCELLED = 2;

	private final CountDownLatch latch;

	private volatile int state;

	private volatile Response response;

	private volatile Throwable exception;

	public FutureResponse() {
		latch = new CountDownLatch(1);
		state = WAITING;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	void setCancelled() {
		state = CANCELLED;
		latch.countDown();
	}

	void setResponse(Response response) {
		this.response = response;
		state = DONE;
		latch.countDown();
	}

	void setThrowable(Throwable exception) {
		this.exception = exception;
		state = DONE;
		latch.countDown();
	}

	@Override
	public boolean isCancelled() {
		return (state == CANCELLED);
	}

	@Override
	public Response get() throws InterruptedException, ExecutionException,
			CancellationException {
		latch.await();
		return innerGet();
	}

	@Override
	public Response get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException,
			CancellationException {
		latch.await(timeout, unit);
		return innerGet();
	}

	private Response innerGet() throws ExecutionException,
			CancellationException {
		if (isCancelled())
			throw new CancellationException();
		else if (exception != null)
			throw new ExecutionException(exception);
		else
			return response;
	}

	@Override
	public boolean isDone() {
		return (state == DONE);
	}

}
