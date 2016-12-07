## Introduction

This is a simple example application demonstrating the use of the WSLimitedModule Play module.

It provides two endpoints for triggering outgoing requests using WSClients that have been limited (or not) by the module.  The rate limiting configuration can be seen [here](https://github.com/Kashoo/ws-limited/blob/master/example/conf/application.conf)

It also runs two [MockServers](http://www.mock-server.com/), on ports 1111 and 2222, to intercept and respond to the fake requests sent out in each case.

The test endpoints available from this application:

- GET `/unlimited` - sends 15 non-limited requests to http://localhost:2222, all of which are fired almost instantaneously.
- GET `/limit-test` - sends 15 requests, limited to 1 every 2 seconds to http://localhost:1111 (will take ~30s)

## Usage

Since this application depends on the WSLimited module, you can publish the module to your local cached dependency repo by returning to the root directory and running this following task against the WSLimited project itself:

`> ./activator publishLocal`

To start the example application, simply run:

`> ./activator run`

Then, issue a request to one of the above mentioned endpoints to observe the result:

`> curl -X GET http://localhost:9000/limit-test`

The first request issued will take a few seconds to compile the project first before processing the request.
