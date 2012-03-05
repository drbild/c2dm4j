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
package org.whispercomm.c2dm4j.impl;

import java.util.Date;

import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.TestMessageFactory;

/**
 * Factory to create {@link ResponseImpl}, {@link UnavailableResponseImpl}, and
 * {@link SuccessResponseImpl} objects for test.
 * 
 * @author David R. Bild
 * 
 */
public class TestResponseFactory {

	private static Date createDate() {
		return new Date(1000000000);
	}

	public static ResponseImpl createResponse() {
		return createResponse(ResponseType.DeviceQuotaExceeded,
				TestMessageFactory.create());
	}

	public static ResponseImpl createResponse(ResponseType type, Message message) {
		return new ResponseImpl(type, message);
	}

	public static UnavailableResponseImpl createUnavailable() {
		return createUnavailable(createDate(), TestMessageFactory.create());
	}

	public static UnavailableResponseImpl createUnavailable(Date date,
			Message message) {
		return new UnavailableResponseImpl(date, message);
	}

	public static SuccessResponseImpl createSuccess() {
		return createSuccess("responseid", TestMessageFactory.create());
	}

	public static SuccessResponseImpl createSuccess(String id, Message message) {
		return new SuccessResponseImpl(id, message);
	}

}
