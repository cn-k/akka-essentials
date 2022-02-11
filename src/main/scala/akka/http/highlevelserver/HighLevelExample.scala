package akka.http.highlevelserver

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._


// step 1
import spray.json._

case class Guitar(make: String, model: String, quantity: Int = 0)

object GuitarDB {
  case class CreateGuitar(guitar: Guitar)
  case class GuitarCreated(id: Int)
  case class FindGuitar(id: Int)
  case object FindAllGuitars
  case class AddQuantity(id: Int, quantity: Int)
  case class FindGuitarsInStock(inStock: Boolean)
}

class GuitarDB extends Actor with ActorLogging {
  import GuitarDB._

  var guitars: Map[Int, Guitar] = Map()
  var currentGuitarId: Int = 0

  override def receive: Receive = {
    case FindAllGuitars =>
      log.info("Searching for all guitars")
      sender() ! guitars.values.toList

    case FindGuitar(id) =>
      log.info(s"Searching guitar by id: $id")
      sender() ! guitars.get(id)

    case CreateGuitar(guitar) =>
      log.info(s"Adding guitar $guitar with id $currentGuitarId")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1

    case AddQuantity(id, quantity) =>
      log.info(s"Trying to add $quantity items for guitar $id")
      val guitar: Option[Guitar] = guitars.get(id)
      val newGuitar: Option[Guitar] = guitar.map {
        case Guitar(make, model, q) => Guitar(make, model, q + quantity)
      }

      newGuitar.foreach(guitar => guitars = guitars + (id -> guitar))
      sender() ! newGuitar

    case FindGuitarsInStock(inStock) =>
      log.info(s"Searching for all guitars ${if(inStock) "in" else "out of"} stock")
      if (inStock)
        sender() ! guitars.values.filter(_.quantity > 0)
      else
        sender() ! guitars.values.filter(_.quantity == 0)

  }
}

//marshalling
trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val guitarFormat = jsonFormat3(Guitar)
}

object HighLevelExample extends App with GuitarStoreJsonProtocol{


  implicit val system = ActorSystem("HighLevelExample")
  implicit val materializer= ActorMaterializer
  import system.dispatcher
  import GuitarDB._
  /*
  setup
*/
  val guitarDb = system.actorOf(Props[GuitarDB], "HighLevelGuitarDb")
  val guitarList = List(
    Guitar("Fender", "Stratocaster"),
    Guitar("Gibson", "Les Paul"),
    Guitar("Martin", "LX1")
  )
  guitarList.foreach{guitar => guitarDb ! CreateGuitar(guitar)}
  implicit val timesout = Timeout(2 seconds)

  val guitarServerRoute=
    path("api" / "guitar"){
      //ALWAYS PUT THE MORE SPESIFIC ROUTE FIRST
      parameter('id.as[Int]) { guitarId: Int =>
        get {
          val guitarsFuture: Future[Option[Guitar]] = (guitarDb ? FindGuitar(guitarId)).mapTo[Option[Guitar]]
          val entityFuture: Future[HttpEntity.Strict] = guitarsFuture.map { guitars =>
            HttpEntity(
              ContentTypes.`application/json`,
              guitars.toJson.prettyPrint
            )
          }
          complete(entityFuture)
        }
      }~
      get{
        val guitarsFuture: Future[List[Guitar]] = (guitarDb ? FindAllGuitars).mapTo[List[Guitar]]
        val entityFuture = guitarsFuture.map{guitars =>
          HttpEntity(
            ContentTypes.`application/json`,
            guitars.toJson.prettyPrint
          )
        }
        complete(entityFuture)
      }
    }~
      path("api" / "guitar" / IntNumber){guitarId:Int =>
        get{
          val guitarsFuture: Future[Option[Guitar]] = (guitarDb ? FindGuitar(guitarId)).mapTo[Option[Guitar]]
          val entityFuture = guitarsFuture.map { guitars =>
            HttpEntity(
              ContentTypes.`application/json`,
              guitars.toJson.prettyPrint
            )
          }
          complete(entityFuture)
        }
      }~
      path("api" / "guitar" / "inventory"){
          get {
            parameter('inStock.as[Boolean]) { inStock: Boolean =>
            val guitarsFuture: Future[List[Guitar]] = (guitarDb ? FindGuitarsInStock(inStock)).mapTo[List[Guitar]]
            val entityFuture = guitarsFuture.map { guitars =>
              HttpEntity(
                ContentTypes.`application/json`,
                guitars.toJson.prettyPrint
              )
            }
            complete(entityFuture)
          }
        }
      }

  def toHttpEntity(payload:String) = HttpEntity(ContentTypes.`application/json`,payload)
  val simplifiedGuitarServerRoute =
    (pathPrefix("api"/ "guitar") & get){
      path("inventory"){
        parameter('inStock.as[Boolean]) { inStock: Boolean =>
          complete(
            (guitarDb ? FindGuitarsInStock(inStock))
              .mapTo[List[Guitar]]
              .map(_.toJson.prettyPrint)
              .map(toHttpEntity)
          )
        }

      }~
        (path(IntNumber) | parameter('id.as[Int])){ guitarId: Int=>

         complete(
           (guitarDb ? FindGuitar(guitarId))
             .mapTo[Option[Guitar]]
             .map(_.toJson.prettyPrint)
             .map(toHttpEntity)
         )
        }~
      pathEndOrSingleSlash{
        complete(
          (guitarDb ? FindAllGuitars)
            .mapTo[List[Guitar]]
            .map(_.toJson.prettyPrint)
            .map(toHttpEntity)
        )
      }
    }
    Http().bindAndHandle(simplifiedGuitarServerRoute,"localhost",8080)
}
