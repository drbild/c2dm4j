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

import org.whispercomm.c2dm4j.async.handler.Context;
import org.whispercomm.c2dm4j.async.handler.ResultDecision;

/**
 * A {@link Context} implementation for {@link Throwable} instances.
 * 
 * @author David R. Bild
 * 
 */
class ThrowableContext extends AbstractContext<Throwable, ResultDecision> {

	/**
	 * Constructs a new {@code ThrowableContext} wrapping the given
	 * {@code FutureResponse} and {@code Throwable}.
	 * 
	 * @param futureResponse
	 *            the {@code FutureResponse} for the throwable
	 * @param message
	 *            the throwable
	 */
	public ThrowableContext(FutureResponse futureResponse, Throwable throwable) {
		super(futureResponse, throwable, ResultDecision.RETURN);
	}

	/**
	 * Constructs a new {@code AbstractContext} from an existing context,
	 * object, and initial decision. The future, delay, and internal context map
	 * are copied. This constructor is useful for transforming, for example, a
	 * {@link MessageContext} into a {@code ThrowableContext} when the exception
	 * is caught.
	 * 
	 * @param context
	 *            the context whose future, delay, and context map to copy.
	 * @param message
	 *            the throwable
	 */
	public ThrowableContext(AbstractContext<?, ?> context, Throwable throwable) {
		super(context, throwable, ResultDecision.RETURN);
	}

}
