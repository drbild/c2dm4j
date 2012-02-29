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

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.async.handler.MessageFilter;
import org.whispercomm.c2dm4j.async.handler.ResponseHandler;
import org.whispercomm.c2dm4j.async.handler.ThrowableHandler;

/**
 * Context associated with an object (i.e., {@link Message}, {@link Response},
 * or {@link Throwable}) in the asynchronous flow. The context holds
 * <ul>
 * <li>the object,</li>
 * <li>the {@link FutureResponse} returned by the
 * {@link AsyncC2dmManager#pushMessage(Message)} method call,</li>
 * <li>the delay before the object should be sent or retried,</li>
 * <li>the decision (delay, retry, return, etc.) for the object, and</li>
 * <li>an object map for {@link MessageFilter}, and {@link ResponseHandler}, and
 * {@link ThrowableHandler} instances to store message-specific context.</li>
 * </ul>
 * 
 * @see MessageContext
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
public interface Context<T, E extends Enum<E>> {

	/**
	 * Gets the object for this context.
	 * 
	 * @return the object for this context.
	 */
	public T unwrap();

	/**
	 * Gets the decision for the associated object (e.g., delay, cancel, or
	 * retry).
	 * 
	 * @return the decision for the associated object
	 */
	public E getDecision();

	/**
	 * Sets the decision for the associated object (e.g., delay, cancel, or
	 * retry).
	 * 
	 * @param e
	 *            the decision for the associated object
	 */
	public void setDecision(E e);

	/**
	 * Gets the duration (in milliseconds) that should elapse before the message
	 * is sent or retried.
	 * 
	 * @return the duration (in milliseconds) before the message should be sent
	 *         or retried
	 */
	public long getDelay();

	/**
	 * Sets the duration (in milliseconds) that should elapse before the message
	 * is sent or retried.
	 * 
	 * @param delay
	 *            the duration (in milliseconds) before the message should be
	 *            sent or retried
	 */
	public void setDelay(long delay);

	/**
	 * Adds an key-value pair to the internal context map. Handlers and filters
	 * should use the following convention. Each handler (filter) may store one
	 * value, using the handler's canonical name
	 * (.getClass().getCanonicalName()) as the key. If multiple instances of the
	 * same type are allowable, the key should be composed of the canonical name
	 * and a instance identifier. Handlers needing multiple values should store
	 * a map.
	 * 
	 * @param key
	 *            the key for the context map--- the handler class's canonical
	 *            name, by convention
	 * @param value
	 *            the value to store--- often a {@code Map}
	 */
	public void put(Object key, Object value);

	/**
	 * Gets the value associated with the key.
	 * 
	 * @see #put(Object, Object)
	 * 
	 * @param key
	 *            the key for the context map--- the handler class's canonical
	 *            name, by convention
	 * @return the value
	 */
	public Object get(Object key);

	/**
	 * Gets the value associated with the key, casting it to the specified type.
	 * This is a helper method to avoid casting the output of
	 * {@link #get(Object)}.
	 * 
	 * @see #put(Object, Object)
	 * 
	 * @param key
	 *            the key for the context map--- the handler class's canonical
	 *            name, by convention
	 * @param returnType
	 *            the type of the returned value
	 * @return the value
	 */
	public <C> C get(Object key, Class<? extends C> returnType);
}
