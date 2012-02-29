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

import java.util.HashMap;
import java.util.Map;

import org.whispercomm.c2dm4j.async.handler.Context;

/**
 * Implementation of {@link Context} useful across all object types.
 * 
 * @see
 * @see ResponseContext
 * @see ThrowableContext
 * 
 * @author David R. Bild
 * 
 * @param <T>
 *            the type of object to which to associate context
 * @param <E>
 *            the enumeration of possible decisions
 */
abstract class AbstractContext<T, E extends Enum<E>> implements Context<T, E> {

	private final FutureResponse futureResponse;

	private final T wrapped;

	private long delay;

	private E decision;

	private Map<Object, Object> map;

	private AbstractContext(FutureResponse futureResponse, T wrapped,
			E defaultDecision, long delay, Map<Object, Object> map) {
		this.wrapped = wrapped;
		this.futureResponse = futureResponse;
		this.decision = defaultDecision;
		this.delay = delay;
		this.map = map;
	}

	/**
	 * Constructs a new {@code AbstractContext} wrapping the given
	 * {@code Future}, object, and initial decision.
	 * 
	 * @param futureResponse
	 *            the {@code FutureResponse} for the object
	 * @param wrapped
	 *            the object
	 * @param defaultDecision
	 *            the initial decision for the object
	 */
	public AbstractContext(FutureResponse futureResponse, T wrapped,
			E defaultDecision) {
		this(futureResponse, wrapped, defaultDecision, 0,
				new HashMap<Object, Object>());
	}

	/**
	 * Constructs a new {@code AbstractContext} from an existing context,
	 * object, and initial decision. The future, delay, and internal context map
	 * are copied. This constructor is useful for transforming, for example, a
	 * {@link MessageContext} into a {@link ResponseContext} when a response is
	 * received.
	 * 
	 * @param context
	 *            the context whose future, delay, and internal context map to
	 *            copy.
	 * @param wrapped
	 *            the object
	 * @param defaultDecision
	 *            the initial decision for the object
	 */
	public AbstractContext(AbstractContext<?, ?> context, T wrapped,
			E defaultDecision) {
		this(context.futureResponse, wrapped, defaultDecision, context.delay,
				context.map);
	}

	@Override
	public T unwrap() {
		return wrapped;
	}

	@Override
	public E getDecision() {
		return decision;
	}

	@Override
	public void setDecision(E decision) {
		this.decision = decision;
	}

	@Override
	public long getDelay() {
		return delay;
	}

	@Override
	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public void put(Object key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	// It's up to callers to provide the correct returnType.
	@SuppressWarnings("unchecked")
	@Override
	public <C> C get(Object key, Class<? extends C> returnType) {
		return (C) map.get(key);
	}

	public FutureResponse getFutureResponse() {
		return futureResponse;
	}
}
