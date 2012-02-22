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
package org.whispercomm.c2dm4j;

/**
 * An enum representing the response from the C2DM service for a message push
 * request. The class encapsulating the response can be retrieved via the
 * {@link #associatedClass()} method.
 * <p>
 * Descriptions for the individual enum values are taken from the <a
 * href="http://code.google.com/android/c2dm/#server">C2DM web page</a>.
 * 
 * @see Response
 * 
 * @author David R. Bild
 * 
 */
public enum ResponseType {
	/**
	 * Message was successfully received by the C2DM service. N.B., this does
	 * not indicate the message has been delivered to the client yet.
	 */
	Success(SuccessResponse.class),
	/**
	 * Indicates that the server is temporarily unavailable (i.e., because of
	 * timeouts, etc). Sender must retry later, honoring any {@code Retry-After}
	 * header included in the response. Application servers must implement the
	 * exponential back off. Senders that create problems risk being
	 * blacklisted.
	 */
	ServiceUnavailable(UnavailableResponse.class),
	/**
	 * Indicates that the <a href=
	 * "http://code.google.com/apis/accounts/docs/AuthForInstalledApps.html"
	 * >ClientLogin</a> {@code AUTH_TOKEN} used to validate the sender is
	 * invalid.
	 */
	Unauthorized,
	/**
	 * Too many messages sent by the sender. Retry after a while.
	 */
	QuotaExceeded,
	/**
	 * Too many messages sent by the sender to a specific device. Retry after a
	 * while.
	 */
	DeviceQuotaExceeded,
	/**
	 * Missing {@code registration_id}. Sender should always add the
	 * {@code registration_id} to the request.
	 */
	MissingRegistration,
	/**
	 * Bad {@code registration_id}. Sender should remove this
	 * {@code registration_id}.
	 */
	InvalidRegistration,
	/**
	 * The {@code sender_id} contained in the {@code registration_id} does not
	 * match the sender id used to register with the C2DM servers.
	 */
	MismatchSenderId,
	/**
	 * The user has uninstalled the application or turned off notifications.
	 * Sender should stop sending messages to this device and delete the
	 * {@code registration_id}. The client needs to re-register with the C2DM
	 * servers to receive notifications again.
	 */
	NotRegistered,
	/**
	 * The payload of the message is too big, see the <a
	 * href="http://code.google.com/android/c2dm/#limitations">limitations</a>.
	 * Reduce the size of the message.
	 */
	MessageTooBig,
	/**
	 * Collapse key is required. Include the collapse key in the request.
	 */
	MissingCollapseKey;

	private Class<? extends Response> clazz;

	private ResponseType() {
		this.clazz = Response.class;
	}

	private ResponseType(Class<? extends Response> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Retrieves the class that encapsulates responses for this response type.
	 * 
	 * @return the class that encapsulates responses for this response type.
	 */
	public Class<? extends Response> associatedClass() {
		return clazz;
	}
}
