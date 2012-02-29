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
package org.whispercomm.c2dm4j.async.handler;

import org.whispercomm.c2dm4j.async.Context;

/**
 * Interface for an exception handler. A method is provided to handle exceptions
 * thrown while processing messages for or response from the C2DM service.
 * 
 * @author David R. Bild
 */
public interface ThrowableHandler<T extends Throwable> {

	/**
	 * Handle an exception thrown while processing a message for or a response
	 * from the C2DM service.
	 * 
	 * @param context
	 *            the context associated with the exception
	 */
	public void handleThrowable(Context<T, ResultDecision> context);

}
