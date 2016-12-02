# Introduction

This module provides a WSClient that supports rate limiting.

# Quick Start

The project includes a self-contained wrapper for [Typesafe Activator](http://www.typesafe.com/activator), which is used to execute [sbt](http://www.scala-sbt.org/)/Play tasks like building and testing the module. For example:

* Execute all tests: `$ ./activator test`

# Using

This module is built upon and therefor has a requirements on:

* Play 2.4.3
* Scala 2.11.7

To include it in a Play service, first ensure you have the correct references to the Kashoo Artifactory dependency resolver 
in the build.sbt.  Next, add a reference to it in your dependency section:

```
"com.kashoo" %% "play-ws-limited" % "1.+" % Compile
```

To gain access to Guice managed BooksApi client factory for providing configured instances of the client, add the following to
 the application.conf: 
 
```play.modules.enabled += "com.kashoo.PlayWsLimitedModule"```

