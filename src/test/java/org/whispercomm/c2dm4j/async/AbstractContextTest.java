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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract unit test for children of {@link AbstractContext}.
 * 
 * @author David R. Bild
 * 
 * @param <T>
 * @param <E>
 */
public abstract class AbstractContextTest<T, E extends Enum<E>> {

	protected abstract AbstractContext<T, E> getContext();

	protected abstract T getWrapped();

	protected abstract E getDefaultDecision();

	protected abstract E getOtherDecision();

	protected abstract FutureResponse getFutureResponse();

	private AbstractContext<T, E> cut;

	@Before
	public void setup() {
		cut = getContext();
	}

	@Test
	public void unwrapReturnsWrappedObject() {
		assertThat(cut.unwrap(), is(getWrapped()));
	}

	@Test
	public void getDecisionReturnsDecision() {
		assertThat(cut.getDecision(), is(getDefaultDecision()));
	}

	@Test
	public void setDecisionUpdatesDecision() {
		cut.setDecision(getOtherDecision());
		assertThat(cut.getDecision(), is(getOtherDecision()));
	}

	@Test
	public void initialDelayis0ms() {
		assertThat(cut.getDelay(), is(0L));
	}

	@Test
	public void setDelayUpdatesDelay() {
		cut.setDelay(123456789L);
		assertThat(cut.getDelay(), is(123456789L));
	}

	@Test
	public void getFutureResponseReturnsFutureResponse() {
		assertThat(cut.getFutureResponse(), is(getFutureResponse()));
	}

	@Test
	public void putIsRetrievalableViaGet() {
		Object obj = new Object();
		cut.put("MyKey", obj);
		assertThat(cut.get("MyKey"), is(obj));
	}

	@Test
	public void getAppliesProperCast() {
		String value = "My Value String";
		cut.put("MyKey", value);
		assertThat(cut.get("MyKey", String.class), is(value));
	}

}
