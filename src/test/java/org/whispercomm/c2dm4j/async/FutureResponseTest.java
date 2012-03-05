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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.Response;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FutureResponse}.
 * 
 * @author David R. Bild
 * 
 */
public class FutureResponseTest {

	private FutureResponse cut;

	@Before
	public void setup() {
		cut = new FutureResponse();
	}

	@Test
	public void cancelReturnsFalse() {
		assertThat(cut.cancel(true), is(false));
		assertThat(cut.cancel(false), is(false));
	}

	@Test(expected = CancellationException.class)
	public void setCancelledLeadsToCancelledException()
			throws CancellationException, InterruptedException,
			ExecutionException {
		cut.setCancelled();
		cut.get();
	}

	@Test
	public void isCancelledReturnsTrueIffCancelled() {
		assertThat(cut.isCancelled(), is(false));
		cut.setCancelled();
		assertThat(cut.isCancelled(), is(true));
	}

	@Test
	public void setResponseLeadsToResponse() throws CancellationException,
			InterruptedException, ExecutionException {
		Response response = mock(Response.class);
		cut.setResponse(response);
		assertThat(cut.get(), is(response));
	}

	@Test
	public void setThrowableLeadsToThrowable() throws CancellationException,
			InterruptedException {
		Throwable throwable = mock(Throwable.class);
		cut.setThrowable(throwable);
		try {
			cut.get();
		} catch (ExecutionException e) {
			assertThat(e.getCause(), is(throwable));
			return;
		}
		fail();
	}

	@Test
	public void setIsDoneReturnsTrueIffResponse() {
		Response response = mock(Response.class);
		assertThat(cut.isDone(), is(false));
		cut.setResponse(response);
		assertThat(cut.isDone(), is(true));
	}

	@Test
	public void setIsDoneReturnsTrueIffThrowable() {
		Throwable throwable = mock(Throwable.class);
		assertThat(cut.isDone(), is(false));
		cut.setThrowable(throwable);
		assertThat(cut.isDone(), is(true));
	}

	@Test
	public void setIsDoneReturnsFalseIfCancelled() {
		cut.setCancelled();
		assertThat(cut.isDone(), is(false));
	}

}
