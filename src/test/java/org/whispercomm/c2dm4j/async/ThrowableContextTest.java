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

import static org.mockito.Mockito.mock;

import org.whispercomm.c2dm4j.async.handler.ResultDecision;

/**
 * Unit tests for {@link ThrowableContext}. The bulk of the testing is performed
 * by the parent class {@link AbstractContextTest}.
 * 
 * @author David R. Bild
 * 
 */
public class ThrowableContextTest extends
		AbstractContextTest<Throwable, ResultDecision> {

	private Throwable throwable;

	private FutureResponse futureResponse;

	@Override
	protected AbstractContext<Throwable, ResultDecision> getContext() {
		throwable = mock(Throwable.class);
		futureResponse = new FutureResponse();
		return new ThrowableContext(futureResponse, throwable);
	}

	@Override
	protected Throwable getWrapped() {
		return throwable;
	}

	@Override
	protected ResultDecision getDefaultDecision() {
		return ResultDecision.RETURN;
	}

	@Override
	protected ResultDecision getOtherDecision() {
		return ResultDecision.RETRY;
	}

	@Override
	protected FutureResponse getFutureResponse() {
		return futureResponse;
	}

}
