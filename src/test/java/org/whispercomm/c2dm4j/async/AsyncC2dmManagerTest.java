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

import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.whispercomm.c2dm4j.C2dmManager;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlers;
import org.whispercomm.c2dm4j.async.handler.AsyncHandlersFactory;

/**
 * Unit tests for (@link AsyncC2dmManager}.
 * 
 * @author David R. Bild
 * 
 */
public class AsyncC2dmManagerTest {

	private ScheduledExecutorService executor;

	private C2dmManager manager;

	private AsyncHandlers handlers;

	private AsyncC2dmManager cut;

	@Before
	public void setup() {
		handlers = AsyncHandlersFactory.createEmpty();

	}

}
