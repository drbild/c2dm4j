# C2DM4j [![Build Status](https://travis-ci.org/drbild/c2dm4j.png?branch=master)](https://travis-ci.org/drbild/c2dm4j)
C2DM4j is a Java library for implementing the third-party application server
component of the [Android Cloud to Device Messaging (C2DM) framework](http://code.google.com/android/c2dm/). It implements features like automated
retries with exponential backoff and supports both synchronous and asynchronous
workflows. The asynchronous flow is extensible via response handlers.

C2DM4j is released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

See the Usage section below for instructions or browse the [javadocs](http://drbild.github.com/c2dm4j/apidocs/index.html).

## Downloads
C2DM4j is available from Maven Central and as a jar file.

+  Maven Central:
        
        <dependency>
            <groupId>org.whispercomm.c2dm4j</groupId>
            <artifactId>c2dm4j</artifactId>
            <version>1.0.2</version>
        </dependency>
        
+  Jar: [c2dm4j-1.0.2.jar](http://github.com/downloads/drbild/c2dm4j/c2dm4j-1.0.2.jar) ([javadoc](http://github.com/downloads/drbild/c2dm4j/c2dm4j-1.0.2-javadoc.jar)) ([source](http://github.com/downloads/drbild/c2dm4j/c2dm4j-1.0.2-sources.jar))

## Dependencies
C2DM4j uses the following libraries:

+  [Apache HttpComponents](https://hc.apache.org/)
+  [SLF4J](http://www.slf4j.org/)
+  [Google Guava](http://code.google.com/p/guava-libraries/) 
+  [Apache Commons IO](http://commons.apache.org/io/)

## Changelog

+  1.0.2 (June 2, 2012)
   +  zero the message delay after dequeuing (David R. Bild, reported by Noriaki Kadota)
+  1.0.1 (March 23, 2012)
   +  use UTF-8 encoding for the body of POST requests sent to C2DM (Noriaki Kadota)
   +  fix project url in pom.xml (David R. Bild)

## Usage
C2DM4j supports two workflow flows, synchronous and asynchronous. In the
synchronous flow, the application thread submitting a message is used to deliver
the message to the C2DM service and no error-handling or retrying is done
automatically. Most users will want the asynchronous flow, in which the
application thread submits a message to a queue and a background thread delivers
the message to C2DM and can automatically response to errors and retries. The
final response is still available to the submitting application thread via a [Future](http://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/Future.html).

Example code follows. Browse the [javadocs](http://drbild.github.com/c2dm4j/apidocs/index.html).

#### Synchronous Quickstart (most users will want the asynchronous quickstart)
The synchronous flow is easy to setup and use, but doesn't automatically handle
any errors.  First, configure the `C2dmManager`.

```java
/* Read the C2DM authentication token from a file */
AuthTokenProvider provider = new FileAuthTokenProvider("/var/myservice/authtoken.dat");

/* Create an HttpClient instance. This HttpClient implementation is not
 * thread-safe, so concurrent calls to C2dmManager.pushMessage() are now allowed.
 */
HttpClient client = new DefaultHttpClient();

/* Create the C2DM Manager */
C2dmManager manager = new DefaultC2dmManager(client, provider);
```

Then send a message to a client.

```java
/* Build message */
Message message = new MessageBuilder().collapseKey("myCollapseKey")
                              .delayWhileIdle(true)
                              .registrationId("registrationKeyFromClient")
                              .put("myKey", "myValue")
                              .build();

/* Send to C2DM, ignoring the response */
manager.pushMessage(message);
```

To handle errors, check the response.

```java
/* Send to C2DM, checking the response */
Response response = manager.pushMessage(message);

switch (response.getResponseType()) {
    case Success:
        // Do nothing
        break;
    case ServiceUnavailable:
        // TODO: Backoff and try again
        break;
    /* TODO: Handle remaining ResponseType values */
    default:
        break;
}
```

#### Asynchronous Flow Quickstart
The asynchronous flow is similar, but a `ScheduledExecutorService` instance is
needed to do message delivery in the background and the `HttpClient` instance must be thread-safe.

This flow will automatically handle exponential backoff and retries for service
unavailable and quota exceeded errors, handle per-device exponential backoff and
retries for device quota exceeded errors, and will respect `Retry-After` headers
on service unavailable errors.

First, configure the `AsyncC2DMManager`.

```java
/* Create an executor backed by MAX_THREADS threads */
int MAX_THREADS = 8;
ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);

/* Create a thread-safe (up to MAX_THREADS) HttpClient instance */
ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
conmManager.setMaxTotal(MAX_THREADS);
connManager.setDefaultMaxPerRoute(MAX_THREADS);
HttpClient client = new DefaultHttpClient(connManager);

/* Read the C2DM authentication token from a file */
AuthTokenProvider provider = new FileAuthTokenProvider("/var/myservice/authtoken.dat");
                
/* Create AsyncC2dmManager instance */
AsyncC2dmManager manager = AsyncC2dmManagerFactory.create(client, provider, executor);
```

Then send a message to a client.

```java
/* Build message */
Message message = new MessageBuilder().collapseKey("myCollapseKey")
                              .delayWhileIdle(true)
                              .registrationId("registrationKeyFromClient")
                              .put("myKey", "myValue")
                              .build();

/* Send to C2DM, ignoring the response */
manager.pushMessage(message);
```

To handle errors not taken care of automatically, check the response.

```java
/* Send to C2DM, checking the response via the Future */
Future<Response> future = manager.pushMessage(message);
        
/* When ready to wait on the response, make a blocking call to future.get() */
Response response = future.get();  // Blocks until response is available

switch(response.getResponseType()) {
    case Success:
        // do nothing
        break;
    case InvalidRegistration:
        // TODO: client has unregistered, so remove
        break;
    /* TODO: Handle remaining ResponseType values */
    default:
        break;
}
```

#### Authentication Token Provider
C2DM uses the [Google ClientLogin API](http://code.google.com/apis/accounts/docs/AuthForInstalledApps.html) for authentication. An authentication
token obtained from Google must be attached to each request. Google may change
the token at any time by attaching a new token to a response. C2DM4j obtains and
persists tokens via an `AuthTokenProvider` . The default provider is `FileAuthTokenProvider`,
which stores the authentication token in a UTF8-encoded file.

Initialize a token file by saving the initial authentication token obtained from
Google into a file.  Then create an instance of `AuthTokenProvider`:

```java
String authTokenFilename = "/var/myservice/authtoken.dat"; 
AuthTokenProvider provider = new FileAuthTokenProvider(authTokenFilename);
```

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

A `Message` instance can be built from the `MessageBuilder` class. The class can
be cloned, to allow initial partial configuration.

```java
/* Prepare the default message builder */
MessageBuilder default = new MessageBuilder().collapseKey("a").delayWhileIdle(true);

/* Use a clone to build up a specific message */
MessageBuilder builder = new MessageBuilder(default).registrationId("registrationKeyFromClient")
                                                    .put("myKey", "myValue");

/* Get the immutable message instance */
Message message = builder.build();
```

#### Custom Asynchronous Handlers
The asynchronous flow can be extended by registering additional `MessageFilter`,
`ResponseHandler`, and `ThrowableHandler` instances with the `AsyncC2dmManager`
instance. A simple example follows. See the code for [`DeviceBackoffThrottle`](https://github.com/drbild/c2dm4j/blob/master/src/main/java/org/whispercomm/c2dm4j/async/handler/DeviceBackoffThrottle.java)
and [`GlobalBackoffThrottle`](https://github.com/drbild/c2dm4j/blob/master/src/main/java/org/whispercomm/c2dm4j/async/handler/GlobalBackoffThrottle.java) for more detailed examples.

Create the `AsyncC2dmManager` instance with a custom set of `AsyncHandlers`.

```java
/* Create a handler to unregister a client on a InvalidRegistration error */
MyClientDatastore clientDatastore; // Assume this was instantiated elsewhere
ResponseHandler<Response> invalidRegHandler = new ResponseHandler<Response>() {
    @Override
    public void handleResponse(Context<Response, ResultDecision> context) {
        Response response = context.unwrap();

	    // Don't retry, just return the error to the future
        context.setDecision(ResultDecision.RETURN);
            
        // Unregister from the client data store
        clientDatastore.unregister(response.getMessage().getRegistrationId());
    }
};

/* Create a set of handlers already populated with those for automatic exponential
 * backoff and retry and add the custom handler.
 */
AsyncHandlers handlers = AsyncHandlersFactory.create();
handlers.appendResponseHandler(ResponseType.InvalidRegistration, invalidRegHandler);

/* Create AsyncC2dmManager instance.
 * client, provider, and executor are created as in the default asynchronous flow example 
 */
AsyncC2dmManager manager = AsyncC2dmManagerFactory.create(client, provider, handlers, executor);
```

## Contributions
Contributions are welcome. Please submit them as pull requests on [GitHub](http://github.com/drbild/c2dm4j).

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
Copyright 2012 The Regents of the University of Michigan

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this work except in compliance with the License. You may obtain a copy of the
License from the LICENSE.txt file or at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.