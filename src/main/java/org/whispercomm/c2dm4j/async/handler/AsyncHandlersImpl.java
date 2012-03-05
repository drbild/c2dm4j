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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.whispercomm.c2dm4j.util.CopyOnWriteArrayListMultimap;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;

/**
 * Implementation of {@code AsyncHandlers}. Can be instantiated using the
 * factory methods in {@link AsyncHandlersFactory}.
 * 
 * @author David R. Bild
 * 
 */
class AsyncHandlersImpl implements AsyncHandlers {
	private CopyOnWriteArrayListMultimap<ResponseType, ResponseHandler<? extends Response>> responseHandlers;

	private CopyOnWriteArrayListMultimap<Class<? extends Throwable>, ThrowableHandler<? extends Throwable>> throwableHandlers;

	private List<MessageFilter> enqueueFilters;

	private List<MessageFilter> dequeueFilters;

	/**
	 * Constructs a new intance.
	 */
	public AsyncHandlersImpl() {
		responseHandlers = CopyOnWriteArrayListMultimap.create();
		throwableHandlers = CopyOnWriteArrayListMultimap.create();
		enqueueFilters = new CopyOnWriteArrayList<MessageFilter>();
		dequeueFilters = new CopyOnWriteArrayList<MessageFilter>();
	}

	@Override
	public void appendEnqueueFilter(MessageFilter filter) {
		enqueueFilters.add(filter);
	}

	@Override
	public void appendDequeueFilter(MessageFilter filter) {
		dequeueFilters.add(filter);
	}

	@Override
	public <T extends Response> void appendResponseHandler(ResponseType type,
			ResponseHandler<T> handler) {
		responseHandlers.put(type, handler);
	}

	@Override
	public <T extends Throwable> void appendThrowableHandler(
			Class<? extends T> type, ThrowableHandler<T> handler) {
		throwableHandlers.put(type, handler);
	}

	public void filterMessageEnqueue(
			Context<Message, MessageDecision> messageContext) {
		for (MessageFilter f : enqueueFilters) {
			f.enqueueFilter(messageContext);
		}
	}

	public void filterMessageDequeue(
			Context<Message, MessageDecision> messageContext) {
		for (MessageFilter f : dequeueFilters) {
			f.dequeueFilter(messageContext);
		}
	}

	// Type safety ensured by only adding elements to the container via the
	// type-parameterized appendResponseHandler() method.
	@SuppressWarnings("unchecked")
	@Override
	public <R extends Response> void handleResponse(
			Context<R, ResultDecision> responseContext) {
		for (ResponseHandler<? extends Response> h : responseHandlers
				.get(responseContext.unwrap().getResponseType())) {
			((ResponseHandler<R>) h).handleResponse(responseContext);
		}
	}

	// Type safety ensured by only adding elements to the container via the
	// type-parameterized appendThrowableHandler() method.
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Throwable> void handleThrowable(
			Context<T, ResultDecision> throwableContext) {
		for (ThrowableHandler<? extends Throwable> h : throwableHandlers
				.get(throwableContext.unwrap().getClass())) {
			((ThrowableHandler<T>) h).handleThrowable(throwableContext);
		}
	}
}
