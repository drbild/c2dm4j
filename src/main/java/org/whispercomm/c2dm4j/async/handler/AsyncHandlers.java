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

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;

/**
 * A collection of {@link MessageFilter}, {@link ResponseHandler}, and
 * {@link ThrowableHandler} objects used to automatically handle certain
 * responses and errors in the asynchronous flow. An instance can be created
 * using the factory methods of {@link AsyncHandlersFactory}.
 * 
 * @see AsyncHandlersFactory
 * @see DeviceBackoffThrottle
 * @see GlobalBackoffThrottle
 * 
 * @author David R. Bild
 * 
 */
public interface AsyncHandlers {
	/**
	 * Appends a new filter to the enqueue filter chain. These filters will be
	 * called before a message is added (or re-added) to the queue.
	 * 
	 * @param filter
	 *            the enqueue filter
	 */
	public void appendEnqueueFilter(MessageFilter filter);

	/**
	 * Appends a new filter to the dequeue filter chain. These fitlers will be
	 * called when a message is dequeued for sending, but before it is sent.
	 * 
	 * @param filter
	 *            the dequeue filter
	 */
	public void appendDequeueFilter(MessageFilter filter);

	/**
	 * Appends a new handler to the response handler chain. The handlers will be
	 * called when a response is received from C2DM. A handler that accepts
	 * multiple types must be registered once for each type.
	 * 
	 * @param type
	 *            the type of response that the handler accepts
	 * @param handler
	 *            the response handler
	 */
	public <T extends Response> void appendResponseHandler(ResponseType type,
			ResponseHandler<T> handler);

	/**
	 * Appends a new handler to the throwable handler chain. The handlers will
	 * be called when an exception is thrown while processing a message or
	 * handling a response. A handler that accepts multiple types must be
	 * registered once for each type.
	 * 
	 * @param type
	 *            the type of throwable that the handler accepts
	 * @param handler
	 *            the throwable handler
	 */
	public <T extends Throwable> void appendThrowableHandler(
			Class<? extends T> type, ThrowableHandler<T> handler);

	/**
	 * Filters the message through the full enqueue filter chain.
	 * 
	 * @param messageContext
	 *            the context of the message to be filtered
	 */
	public void filterMessageEnqueue(
			Context<Message, MessageDecision> messageContext);

	/**
	 * Filters the message through the full dequeue filter chain.
	 * 
	 * @param messageContext
	 *            the context of the message to be filtered
	 */
	public void filterMessageDequeue(
			Context<Message, MessageDecision> messageContext);

	/**
	 * Passes the response to all handlers registered for the response type.
	 * 
	 * @param responseContext
	 *            the context of the response to be handled
	 */
	public <R extends Response> void handleResponse(
			Context<R, ResultDecision> responseContext);

	/**
	 * Passes the exception to all handlers registered for the exception type.
	 * 
	 * @param throwableContext
	 *            the context of the exception to be handled
	 */
	public <T extends Throwable> void handleThrowable(
			Context<T, ResultDecision> throwableContext);
}
