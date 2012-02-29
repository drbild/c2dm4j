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
import org.whispercomm.c2dm4j.TestMessageFactory;
import org.whispercomm.c2dm4j.impl.TestResponseFactory;

/**
 * Factory to create {@link MessageContext}, {@link ResponseContext}, and
 * {@link ThrowableContext} objects for test.
 * 
 * @author David R. Bild
 * 
 */
public class TestContextFactory {

	public static MessageContext createMessageContext() {
		return createMessageContext(TestMessageFactory.create());
	}

	public static MessageContext createMessageContext(Message message) {
		return new MessageContext(new FutureResponse(), message);
	}

	public static ResponseContext createResponseContext() {
		return createResponseContext(TestResponseFactory.createResponse());
	}

	public static ResponseContext createResponseContext(Response response) {
		return new ResponseContext(new FutureResponse(), response);
	}

	public static ThrowableContext createThrowableContext() {
		return new ThrowableContext(new FutureResponse(), new Exception(
				"exception"));
	}

	public static ThrowableContext createThrowableContext(Throwable t) {
		return new ThrowableContext(new FutureResponse(), t);
	}

}
