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

import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.async.Context;
import java.util.concurrent.Future;

/**
 * Enumeration of valid decisions for {@link Response} and {@link Throwable}
 * objects.
 * 
 * @author David R. Bild
 * 
 */
public enum ResultDecision {
	/**
	 * Retry the message, after any delay indicated in the {@link Context}.
	 */
	RETRY,
	/**
	 * Return the {@link Response} or {@link Throwable} to the {@link Future}.
	 */
	RETURN
}
