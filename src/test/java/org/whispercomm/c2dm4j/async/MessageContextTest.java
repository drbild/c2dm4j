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
import org.whispercomm.c2dm4j.MessageBuilder;
import org.whispercomm.c2dm4j.async.handler.MessageDecision;

/**
 * Unit tests for {@link MessageContext}. The bulk of the testing is performed
 * by the parent class {@link AbstractContextTest}.
 * 
 * @author David R. Bild
 * 
 */
public class MessageContextTest extends
		AbstractContextTest<Message, MessageDecision> {

	private Message message;

	private FutureResponse futureResponse;

	@Override
	protected AbstractContext<Message, MessageDecision> getContext() {
		message = new MessageBuilder().collapseKey("1").registrationId("1")
				.build();
		futureResponse = new FutureResponse();
		return new MessageContext(futureResponse, message);
	}

	@Override
	protected Message getWrapped() {
		return message;
	}

	@Override
	protected MessageDecision getDefaultDecision() {
		return MessageDecision.SEND;
	}

	@Override
	protected MessageDecision getOtherDecision() {
		return MessageDecision.CANCEL;
	}

	@Override
	protected FutureResponse getFutureResponse() {
		return futureResponse;
	}

}
