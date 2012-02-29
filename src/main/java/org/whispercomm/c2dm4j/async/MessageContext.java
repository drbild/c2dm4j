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
import org.whispercomm.c2dm4j.async.handler.MessageDecision;

/**
 * A {@link Context} implementation for {@link Message} instances.
 * 
 * @author David R. Bild
 * 
 */
class MessageContext extends AbstractContext<Message, MessageDecision> {

	/**
	 * Constructs a new {@code MessageContext} wrapping the given
	 * {@code FutureResponse} and {@code Message}.
	 * 
	 * @param futureResponse
	 *            the {@code FutureResponse} for the message
	 * @param message
	 *            the message
	 */
	public MessageContext(FutureResponse futureResponse, Message message) {
		super(futureResponse, message, MessageDecision.SEND);
	}

	/**
	 * Constructs a new {@code AbstractContext} from an existing context,
	 * object, and initial decision. The future, delay, and internal context map
	 * are copied. This constructor is useful for transforming, for example, a
	 * {@link ResponseContext} into a {@code MessageContext} when the message
	 * should be retried.
	 * 
	 * @param context
	 *            the context whose future, delay, and context map to copy.
	 * @param message
	 *            the message
	 */
	public MessageContext(AbstractContext<?, ?> context, Message message) {
		super(context, message, MessageDecision.SEND);
	}

}
