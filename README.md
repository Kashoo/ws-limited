# Introduction

This module provides a WSClient that supports rate limiting.

# Quick Start

The project includes a self-contained wrapper for [Typesafe Activator](http://www.typesafe.com/activator), which is used to execute [sbt](http://www.scala-sbt.org/)/Play tasks like building and testing the module. For example:

* Execute all tests: `$ ./activator test`

# Using

This module is built upon and therefor has a requirements on:

* Play 2.4.8
* Scala 2.11.8

To include it in a Play service, first ensure you have the correct references to the Kashoo Artifactory dependency resolver
in the build.sbt.  Next, add a reference to it in your dependency section:

```
"com.kashoo" %% "ws-limited" % "0.1.0" % Compile
```

To gain access to Guice managed BooksApi client factory for providing configured instances of the client, add the following to
 the application.conf:

```play.modules.enabled += "com.kashoo.ws.WSLimitedModule"```

Once the module has been enabled in your project, you can instantiate a rate limited WSClient by using the `@RateLimited` annotation on an injected dependency:

```
@Singleton
class ExampleController @Inject() (@RateLimited ws: WSClient) extends Controller {

  ...
}
```


# Configuration

Below is an very simple example configuration for applying a limit to outgoing requests to `example.com`:

```
play.modules.enabled += "com.kashoo.ws.WSLimitedModule"

com.kashoo.ws.rates = {
  exampleRate = {
    queries = 1
    period = "10 seconds"
  }
}

com.kashoo.ws.request-limits = [
  {
    rate = "exampleRate"
    host = "example.com"
  }
]
```
