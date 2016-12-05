package application

import com.google.inject.{Inject, Singleton}
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Starts two mock servers on ports 1111 and 2222 to intercept calls made from the ExampleController endpoints
  */
@Singleton
class MockServer @Inject() (lifecycle: ApplicationLifecycle) {

  val mockServer = {
    Logger.info("Starting mock server...")
    startClientAndServer(1111)
  }

  val mockServer2 = {
    Logger.info("Starting mock server 2...")
    startClientAndServer(2222)
  }

  mockServer.when(request(".*")).respond(response().withStatusCode(200).withBody({
    Logger.info("received mock request")
    "response"
  }))

  mockServer2.when(request(".*")).respond(response().withStatusCode(200).withBody({
    Logger.info("received mock request")
    "response"
  }))

  lifecycle.addStopHook { () =>
    Future.successful({
      mockServer.stop()
      mockServer2.stop()
    })
  }
}
