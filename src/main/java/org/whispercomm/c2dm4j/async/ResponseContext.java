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
package org.whispercomm.c2dm4j.async;

import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.async.handler.Context;
import org.whispercomm.c2dm4j.async.handler.ResultDecision;

/**
 * A {@link Context} implementation for {@link Response} instances.
 * 
 * @author David R. Bild
 * 
 */
class ResponseContext extends AbstractContext<Response, ResultDecision> {

	/**
	 * Constructs a new {@code ResponseContext} wrapping the given
	 * {@code FutureResponse} and {@code Response}.
	 * 
	 * @param futureResponse
	 *            the {@code FutureResponse} for the response
	 * @param message
	 *            the response
	 */
	public ResponseContext(FutureResponse futureResponse, Response response) {
		super(futureResponse, response, ResultDecision.RETURN);
	}

	/**
	 * Constructs a new {@code AbstractContext} from an existing context,
	 * object, and initial decision. The future, delay, and internal context map
	 * are copied. This constructor is useful for transforming, for example, a
	 * {@link MessageContext} into a {@code ResponseContext} when the response
	 * is received.
	 * 
	 * @param context
	 *            the context whose future, delay, and context map to copy.
	 * @param message
	 *            the response
	 */
	public ResponseContext(AbstractContext<?, ?> context, Response response) {
		super(context, response, ResultDecision.RETURN);
	}

}
