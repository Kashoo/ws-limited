# WSLimited Play 2 Module

## Introduction

A Scala [Play framework](https://www.playframework.com/) module supporting simple, client-side rate limiting of requests made through [Play WS](https://www.playframework.com/documentation/2.4.x/ScalaWS)

Currently the library only supports Play 2.4.x.

The library allows rate limiting to be applied by defining simple application configuration.  For example:

```
ws.limited.rates = {
  exampleRate = {
    queries = 1
    period = "10 seconds"
  }
}

ws.limited.policies = [
  {
    rate = "exampleRate"
    host = "example.com"
  }
]
```

## Usage

To get started using the library, first add it as a dependency to your Play project in the build.sbt:

`libararyDependencies += "com.kashoo" %% "ws-limited" % "0.1.0" % Compile`

Next, the simplest way to get started is to include the Play module that provides rate limited clients via dependency injection.  To do this, first add the module to your Play application.conf:

`play.modules.enabled += "com.kashoo.ws.WSLimitedModule"`

Once the module has been enabled in your project, you can get a rate limited WSClient by using the `@RateLimited` annotation on an injected dependency:

```
@Singleton
class ExampleController @Inject() (@RateLimited ws: WSClient) extends Controller {

  ...
}
```

Included in this repo is an [example](https://github.com/Kashoo/ws-limited/tree/master/example) Play application that demonstrates the above usage of the WSLimitedModule.

The library may also be used programmatically and independently of the WSLimitedModule.  A demonstration of this can be seen in the [WSLimitedIntegrationTest](https://github.com/Kashoo/ws-limited/blob/master/test/com/kashoo/ws/WSLimitedIntegrationTest.scala#L45).

## Configuration

When using the WSLimitedModule to provide WSClient dependencies, all provided clients will be rate limited based on configuration gleaned from application.conf (or whatever application configuration file you have provided to your application).  The configuration expects the following elements:

- `ws.limited.rates` - This is expected to be an object/map containing rate names to defined rates.  Each defined rate must have both a `queries` and `period` attribute.  The period should be defined as a String but will be parsed as a [Duration](http://www.scala-lang.org/api/2.11.0/index.html#scala.concurrent.duration.Duration).  Attempting to define a rate without either required field will throw an `IllegalStateException` upon loading of the configuration.  In this way, defining rates are decoupled from defining rate limiting policies that may refer to them.

- `ws.limited.policies` - This is expected to be an array of rate limiting policies.  Each policy must include a `rate` name, which _must_ exist as a defined rate in `ws.limited.rates` and `host` field to match outgoing requests against.  Rate limit policies also include support for two optional fields:  `port` (Int) and `path` (String).  Requests are matched based on the most specific policy they match.  If two policies are defined with the same host and port, the more specific (longer) path length will used to select the more specific policy.

- `ws.limited.execution-context` - Optional setting to allow more explicit configuration of the execution context that any blocked futures will be run against.  It is expected to be a valid thread pool executor configuration.  If not provided, defaults to Play's default execution context.  See [Plays guide on using alternate contexts](https://www.playframework.com/documentation/2.4.x/ThreadPools#Using-other-thread-pools) for more info.  Also see the Limitations section below for some words of caution around using blocking threads in general.  An example:

```
ws.limited.execution-context = {
  fork-join-executor {
    parallelism-factor = 20.0
    parallelism-max = 200
  }
}
```

## Limitations

The main limitation of this library is that it relies on Guava's RateLimiter under the hood, which of course uses blocking to achieve the desired effect.  The code will remain asynchronous via Futures, but there will be threads blocking in the underlying implementation.  Because of this, care must still be taken not to issue requests at a rate much greater than a configured rate limit, as it will surely consume all available thread resources and eventually lead to potential deadlock.  Also for this reason, the thread pool used for executing tasks that will be subject to such blocking is exposed for configuration.  Providing a custom, dedicated thread pool for handling the blocking threads may provide for better performance.  [This resource](http://blog.jessitron.com/2014/01/choosing-executorservice.html) is helpful for understanding how to choose an appropriate thread pool.  The intention is to remove this limitation all together in a future version of the library.

## License

[MIT License](https://github.com/Kashoo/ws-limited/blob/master/LICENSE)
