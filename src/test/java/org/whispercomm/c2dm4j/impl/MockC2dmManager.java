package org.whispercomm.c2dm4j.impl;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.whispercomm.c2dm4j.C2dmManager;
import org.whispercomm.c2dm4j.Message;
import org.whispercomm.c2dm4j.Response;
import org.whispercomm.c2dm4j.ResponseType;
import org.whispercomm.c2dm4j.UnexpectedResponseException;
import org.whispercomm.c2dm4j.auth.AuthTokenException;

/**
 * Mock of C2dmManager suitable for testing code that requires a C2dmManager.
 * Responses to {@link #pushMessage(Message)} are configured by a queue of
 * {@link ResponseType} types. ResponseTypes are added to the FIFO queue via
 * {@link #enqueue(Response)} . Each call to {@link #pushMessage(Message)}
 * removes the type at the head of the queue and returns a {@code Response}
 * object of that type.
 * 
 * @author David R. Bild
 * 
 */
public class MockC2dmManager implements C2dmManager {

	/**
	 * Queue of response types to send. {@link #enqueue(ResponseType)} adds.
	 * {@link #pushMessage(Message)} removes.
	 * 
	 * @see {@link #enqueue(ResponseTye)}
	 */
	private Queue<ResponseType> responses;

	/**
	 * 
	 */
	public MockC2dmManager() {
		responses = new ConcurrentLinkedQueue<ResponseType>();
	}

	/**
	 * Adds the response type to the queue.
	 * 
	 * @param responseType
	 *            the response type to enqueue.
	 */
	public void enqueue(ResponseType responseType) {
		responses.add(responseType);
	}

	@Override
	public Response pushMessage(Message msg)
			throws UnexpectedResponseException, AuthTokenException, IOException {
		return TestResponseFactory.createResponse(responses.remove(), msg);
	}

}
