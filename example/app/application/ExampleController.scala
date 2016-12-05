package application

import com.google.inject.{Inject, Singleton}
import com.kashoo.ws.RateLimited
import play.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class ExampleController @Inject() (@RateLimited ws: WSClient) extends Controller {

  val logger = Logger.of("limit-test")

  def test = Action.async { implicit request =>
    logger.info(s"Using client: ${ws.getClass}")
    Future.sequence(Range(0, 3) map { i =>
      ws.url(s"http://localhost:1111/$i").get().map { result =>
        logger.info("received response from http://localhost:1111")
      }
    }) map { results =>
      Ok("Finished sending 3 requests to http://localhost:1111")
    }
  }

  def testNonLimited = Action.async { implicit request =>
    logger.info(s"Using client: ${ws.getClass}")
    Future.sequence(Range(0, 15) map { i =>
      ws.url(s"http://localhost:2222/$i").get().map { result =>
        logger.info("received response from http://localhost:2222")
      }
    }) map { results =>
      Ok("Finished sending 15 requests to http://localhost:2222")
    }
  }
}
