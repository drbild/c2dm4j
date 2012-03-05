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

/**
 * Interface for a message filter. Methods are provided for filtering when
 * enqueueing or dequeueing the message.
 * 
 * @author David R. Bild
 * 
 */
public interface MessageFilter {
	/**
	 * Filter the message before it is placed in the queue.
	 * 
	 * @param context
	 *            the context associated with the message
	 */
	public void enqueueFilter(Context<Message, MessageDecision> context);

	/**
	 * Filter the message after it is dequeued to be sent, but before it is
	 * actually sent.
	 * 
	 * @param context
	 *            the context associated with the message
	 */
	public void dequeueFilter(Context<Message, MessageDecision> context);

}
