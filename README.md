# C2DM4j
C2DM4j is a Java library for implementing the third-party application server
component of the [Android Cloud to Device Messaging (C2DM) framework](http://code.google.com/android/c2dm/). It implements features like automated
retries with exponential backoff and supports both synchronous and asynchronous
workflows. The asynchronous flow is extensible via response handlers.

C2DM4j is released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

Take a look at [http://drbild.github.com/c2dm4j/](http://drbild.github.com/c2dm4j/)
for complete usage instructions and documentation.

## Downloads
C2DM4j will be available from Maven Central and as a jar file (optionally bundled
with third-party dependencies).

+  Maven Central:
        
        <dependency>
            <groupId>org.whispercomm.c2dm4j</groupId>
            <artifactId>c2dm4j</artifactId>
            <version>0.9</version>
        </dependency>
        
+  Jar without third-party dependencies: [c2dm4j-0.9-without-deps.jar](http://example.com/c2dm4j-0.9-without-deps.jar)

+  Jar with third-party dependencies: [c2dm4j-0.9-with-deps.jar](http://example.com/c2dm4j-0.9-with-deps.jar)


## Dependencies
C2DM4j uses the following libraries:

+  [Apache HttpComponents](https://hc.apache.org/)
+  [SLF4J](http://www.slf4j.org/)
+  [Google Guava](http://code.google.com/p/guava-libraries/) 
+  [Apache Commons IO](http://commons.apache.org/io/)

## Usage
C2DM4j supports two workflows, synchronous and asynchronous. In the synchronous
flow, the application thread submitting a message is used to deliver it to the
C2DM service, blocking while the communication is performed. The application
code is then responsible for responding to errors (e.g., waiting and retrying if
the device quota was exceeded). Most users will want the asynchronous flow
instead. An application thread submits a message to a queue, from which
background worker threads deliver messages to C2DM. If desired, the application
thread can access the C2DM response via a standard Java `Future`. "Handlers" can
be registered with the asynchronous framework to automatically respond to
certain responses or errors.
 
Additional documentation may be found on the 
[C2DM4j website](http://drbild.github.com/c2dm4j/).

#### Authentication Token Provider
C2DM uses the [Google ClientLogin API](http://code.google.com/apis/accounts/docs/AuthForInstalledApps.html) for authentication. An authentication
token obtained from Google must be attached to each request. Google may change
the token at any time by attaching a new token to a response. C2DM4j obtains and
persists tokens via an `AuthTokenProvider` . The default provider is `FileAuthTokenProvider`,
which stores the authentication token in a UTF8-encoded file.

Initialize a token file by saving the initial authentication token obtained from
Google into a file.  Then create an instance of `AuthTokenProvider`:

        String authTokenFilename = "/var/myservice/authtoken.dat"; 
        AuthTokenProvider provider = new FileAuthTokenProvider(authTokenFilename);

The contents of `"/var/myservice/authtoken.dat"` will be used as the initial
token and any changes will be persisted to the same file.


#### C2DM Message
A message to be pushed to a client is represented by a `Message` instance. This
instance contains the meta-data needed by the C2DM service

+  the registration id of the client
+  the collapse key
+  the delay-while-idle flag

and the data to be delivered to the client

+  key-value pairs.

A `Message` instance can be built from the `MessageBuilder` class:

        // Prepare the message
        MessageBuilder builder = new MessageBuilder().collapseKey("a").delayWhileIdle(true);
        builder.registrationId(registrationId);
        builder.put("key", "value");
        
        // Get the immutable instance
        Message message = builder.build();

#### Synchronous Flow
`C2dmManager` is the primary interface for the synchronous flow. The default
implementation has two dependencies, an `AuthTokenProvider` instance, covered in
the prior section, and a `HttpClient` instance. The manager is thread-safe only
if the provided `HttpClient` instance is.

Create an instance of `C2dmManager` :

        // Create an HttpClient instance that is thread-safe up to MAX_THREADS threads
        int MAX_THREADS = 8;
		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
        conmManager.setMaxTotal(MAX_THREADS);
        connManager.setDefaultMaxPerRoute(MAX_THREADS);
        HttpClient client = new DefaultHttpClient(connManager);

	    // Create the C2dmManager instance
        C2dmManager manager = new DefaultC2dmManager(client, provider);

And send a message, handling the response:

	    // Send message
        Response response = manager.pushMessage(message);
        
        // Handle response
        // See the ResponseType javadoc for a full list of possible responses 
        switch (response.getResponseType()) {
        case SUCCESS:
            // Do nothing
            break;
        case SERVICE_UNAVAILABLE:
            // Backoff and try again
            break;
        default:
            break;
        }

#### Asynchronous Flow
`AsyncC2dmManager` is the primary interface for the asynchronous flow. The
default implementation includes global exponential backoff for service
unavailable and quota exceeded responses, per-device exponential backoff for
device quota exceeded responses, and respects 'Retry-After' headers. It
has three dependencies, `AuthTokenProvider` and `HttpClient` , like the
synchronous flow, and `ScheduledExecutorService` . The `HttpClient` instance
must be thread-safe up to the number of threads backing the executor.

Create an instance of `AsyncC2dmManager`:
	
	    // Create an HttpClient instance that is thread-safe up to MAX_THREADS threads
        int MAX_THREADS = 8;
		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
        conmManager.setMaxTotal(MAX_THREADS);
        connManager.setDefaultMaxPerRoute(MAX_THREADS);
        HttpClient client = new DefaultHttpClient(connManager);
        
        // Create a ScheduledExecutorService backed by MAX_THREADS threads
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
        
        // Create AsyncC2dmManager instance
        AsyncC2dmManager manager = AsyncC2dmManagerFactory.create(client, provider, executor);
        
And send a message, handling the response when desired:

        // Send message
        Future<Response> future = manager.pushMessage(message);
        
        // Do some other work
        
        // Handle response, if/when desired (this could be on a different thread)
        Response response = future.get();  // Blocks until response is available
        switch(response.getResponseType()) {
        case SUCCESS:
        	// do nothing
        	break;
        case INVALID_REGISTRATION:
            // client has unregistered; remove
            break;
        default:
            break;
        }

#### Custom Asynchronous Handlers
The asynchronous flow can be extended by registering additional `MessageFilter`, 
`ResponseHandler` , and `ThrowableHandler` instances. Most users will not need
these features. Full documentation is available in the `AsyncHandlers` javadoc
and the [C2DM4j website](http://drbild.github.com/c2dm4j/).

## Contributions
Contributions are welcome. Please submit them as pull requests on [GitHub](http://github.com/drbild/C2DM4j).

## Development
C2DM4j uses Maven as its build tool. Source code resides in the `src/main/java/`
directory. Unit tests reside in the `src/test/java` directory. To run the unit
tests, execute `mvn test`.

To develop in Eclipse, create the `.classpath` and `.project` files by executing `mvn eclipse:eclipse`.
Then import the project into your workspace.

##Authors
**David Bild**

+ [http://www.davidbild.org](http://www.davidbild.org)
+ [http://github.com/drbild](http://github.com/drbild)

##License
Copyright 2012 The University of Michigan

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this work except in compliance with the License. You may obtain a copy of the
License from the LICENSE.txt file or at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.