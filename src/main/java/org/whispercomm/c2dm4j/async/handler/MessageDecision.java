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
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

/**
 * Enumeration of valid decisions for {@link Message} objects.
 * 
 * @author David R. Bild
 * 
 */
public enum MessageDecision {
	/**
	 * Send the message, after any delay indicated in the {@link Context}
	 */
	SEND,
	/**
	 * Cancel the message, returning a {@link CancellationException} in the
	 * associated {@link Future}.
	 */
	CANCEL
}
