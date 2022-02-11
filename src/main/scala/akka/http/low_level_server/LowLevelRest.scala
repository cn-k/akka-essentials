package akka.http.low_level_server

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
//marshalling
import spray.json._

case class Guitar(make: String, model: String)

object GuitarDB {

  case class CreateGuitar(guitar: Guitar)

  case class GuitarCreated(id: Int)

  case class findGuitar(id: Int)

  case object findAllGuitars

}

class GuitarDB extends Actor with ActorLogging {

  import GuitarDB._

  var guitars: Map[Int, Guitar] = Map()
  var currentGuitarId: Int = 0

  override def receive: Receive = {
    case findAllGuitars =>
      log.info("searching or all guitars")
      sender() ! guitars.values.toList
    case findGuitar(id) =>
      log.info(s"Searching guitar by id $id")
      sender() ! guitars.get(id)
    case CreateGuitar(guitar) =>
      log.info(s"Adding guitar $guitar with id $currentGuitarId")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1
  }
}

//marshalling
trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val guitarFormat = jsonFormat2(Guitar)
}

object LowLevelRest extends App with GuitarStoreJsonProtocol {

  import GuitarDB._

  implicit val system = ActorSystem("LowLevelRest")
  implicit val materializer = ActorMaterializer

  import system.dispatcher

  val simpleGuitar = Guitar("Fender", "Stratocaster")
  println(simpleGuitar.toJson.prettyPrint)
  //unmarshalling
  val simpleGuitarJsonString =
    """
      |{
      |  "make": "Fender",
      |  "model": "Stratocaster"
      |}
    """.stripMargin
  println(simpleGuitarJsonString.parseJson.convertTo[Guitar])
  /*
    setup
  */
  val guitarDb = system.actorOf(Props[GuitarDB], "LowLevelGuitarDb")
  val guitarList = List(
    Guitar("Fender", "Stratocaster"),
    Guitar("Gibson", "Les Paul"),
    Guitar("Martin", "LX1")
  )
  guitarList.foreach{guitar => guitarDb ! CreateGuitar(guitar)}
  /*
  server code
  */
  implicit val defaultTimeout = Timeout(2 seconds)
  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/api/guitar"), _, _, _) =>
      val guitarsFuture: Future[List[Guitar]] = (guitarDb ? findAllGuitars).mapTo[List[Guitar]]
      guitarsFuture.map { guitars =>
        HttpResponse(
          entity = HttpEntity(
            ContentTypes.`application/json`,
            guitars.toJson.prettyPrint
          )
        )
      }
    case HttpRequest(HttpMethods.POST, Uri.Path("/api/guitar"), _, entity, _) =>
      val strictEntityFuture: Future[HttpEntity.Strict] = entity.toStrict(3 seconds)
      strictEntityFuture.flatMap{strictEntity =>
        val guitarJsonString: String = strictEntity.data.utf8String
        val guitar: Guitar = guitarJsonString.parseJson.convertTo[Guitar]
        val guitarCreatedFuture: Future[GuitarCreated] = (guitarDb ? CreateGuitar(guitar)).mapTo[GuitarCreated]
        guitarCreatedFuture.map{ _ =>
          HttpResponse(StatusCodes.OK)
        }
      }
    case request:HttpRequest =>
      request.discardEntityBytes()
      Future{
        HttpResponse(status = StatusCodes.NotFound)
      }
  }
  Http().bindAndHandleAsync(requestHandler,"localhost",8080)

}
