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

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for constructing immutable {@link Message} instances.
 * {@link #collapseKey(String)} and {@link #registrationId(String)} must be set
 * before calling {@link #build()}.
 * <p>
 * Example (note that MyClient is made-up class):<br/>
 * <code>
 * // Shared for all messages <br/> 
 * MessageBuilder default = new MessageBuilder().collapseKey("a").delayWhileIdle(true); </br>
 * for (MyClient client : myClients) { </br>
 * &nbsp;&nbsp;&nbsp;&nbsp; // Create copy with message-specific info </br>
 * &nbsp;&nbsp;&nbsp;&nbsp; MessageBuilder mb = new MessageBuilder(default).registrationId(client.id).put(client.key, client.value); </br>
 * &nbsp;&nbsp;&nbsp;&nbsp; // Assume that C2dmManager c2dmManager was declared elsewhere </br>
 * &nbsp;&nbsp;&nbsp;&nbsp; c2dmManager.pushMessage(mb.build()); </br>
 * }
 * </code>
 * 
 * @author David R. Bild
 * 
 */
public class MessageBuilder {

	private String registrationId;

	private String collapseKey;

	private HashMap<String, String> data;

	private boolean delayWhileIdle;

	/**
	 * Constructs a new, empty {@code MessageBuilder}.
	 */
	public MessageBuilder() {
		this.registrationId = null;
		this.collapseKey = null;
		this.data = new HashMap<String, String>();
		this.delayWhileIdle = false;
	}

	/**
	 * Constructs a new {@code MessageBuilder}, copying all fields from the
	 * specified builder.
	 * 
	 * @param that
	 *            the builder whose fields to copy
	 */
	public MessageBuilder(MessageBuilder that) {
		this.registrationId = that.registrationId;
		this.collapseKey = that.collapseKey;
		this.data = new HashMap<String, String>(that.data);
		this.delayWhileIdle = that.delayWhileIdle;
	}

	/**
	 * Constructs a {@code Message} from the builder.
	 * 
	 * @return the newly constructed message.
	 */
	public Message build() {
		if (registrationId == null)
			throw new IllegalStateException(
					"Required parameter 'Registration Id' was not specified.");
		if (collapseKey == null)
			throw new IllegalStateException(
					"Required parameter 'Collapse Key' was not specified.");
		return new MessageImpl(registrationId, collapseKey, data,
				delayWhileIdle);
	}

	/**
	 * Sets the registration id for future messages.
	 * 
	 * @param registrationId
	 *            the registration id for future messages
	 * @return this builder
	 */
	public MessageBuilder registrationId(String registrationId) {
		this.registrationId = registrationId;
		return this;
	}

	/**
	 * Sets the collapse key for future messages.
	 * 
	 * @param collapseKey
	 *            the collapse key for future messages
	 * @return this builder
	 */
	public MessageBuilder collapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
		return this;
	}

	/**
	 * Configures the delayWhileIdle flag for future messages
	 * 
	 * @param delayWhileIdle
	 *            the delayWhileIdle flag for future messages
	 * @return this builder
	 */
	public MessageBuilder delayWhileIdle(boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
		return this;
	}

	/**
	 * Adds a key-value pair to the payload data for future messages.
	 * 
	 * @param key
	 *            the key for the payload pair
	 * @param value
	 *            the value for the payload pair
	 * @return this builder
	 */
	public MessageBuilder put(String key, String value) {
		this.data.put(key, value);
		return this;
	}

	/**
	 * Adds a collection of key-value pairs to the payload data for future
	 * messages.
	 * 
	 * @param map
	 *            the collection of key-value pairs to be added
	 * @return this builder
	 */
	public MessageBuilder putAll(Map<? extends String, ? extends String> map) {
		this.data.putAll(map);
		return this;
	}

	/**
	 * Removes the specified key-value pair from the payload data of future
	 * messages.
	 * 
	 * @param key
	 *            the key of the pair to remove
	 * @return this builder
	 */
	public MessageBuilder remove(String key) {
		this.data.remove(key);
		return this;
	}

	@Override
	public String toString() {
		return String
				.format("Message(registrationId=\"%s\", collapseKey=\"%s\", delayWhileIdle=%b, data=%s)",
						registrationId, collapseKey, delayWhileIdle, data);
	}

	/**
	 * Implementation of {@link Message} returned by
	 * {@link MessageBuilder#build()}.
	 * 
	 * @author David R. Bild
	 * 
	 */
	private static class MessageImpl implements Message {

		private final String registrationId;

		private final String collapseKey;

		private final HashMap<String, String> data;

		private final boolean delayWhileIdle;

		public MessageImpl(String registrationId, String collapseKey,
				Map<String, String> data, boolean delayWhileIdle) {
			this.registrationId = registrationId;
			this.collapseKey = collapseKey;
			this.data = new HashMap<String, String>(data);
			this.delayWhileIdle = delayWhileIdle;
		}

		@Override
		public String getRegistrationId() {
			return registrationId;
		}

		@Override
		public String getCollapseKey() {
			return collapseKey;
		}

		@Override
		public Map<String, String> getData() {
			return new HashMap<String, String>(data);
		}

		@Override
		public boolean delayWhileIdle() {
			return delayWhileIdle;
		}

		@Override
		public String toString() {
			return String
					.format("Message(registrationId=\"%s\", collapseKey=\"%s\", delayWhileIdle=%b, data=%s)",
							registrationId, collapseKey, delayWhileIdle, data);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((collapseKey == null) ? 0 : collapseKey.hashCode());
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			result = prime * result + (delayWhileIdle ? 1231 : 1237);
			result = prime
					* result
					+ ((registrationId == null) ? 0 : registrationId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MessageImpl other = (MessageImpl) obj;
			if (collapseKey == null) {
				if (other.collapseKey != null)
					return false;
			} else if (!collapseKey.equals(other.collapseKey))
				return false;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			if (delayWhileIdle != other.delayWhileIdle)
				return false;
			if (registrationId == null) {
				if (other.registrationId != null)
					return false;
			} else if (!registrationId.equals(other.registrationId))
				return false;
			return true;
		}

	}

}
