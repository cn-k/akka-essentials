package akka.http.highlevelserver


import scala.util.Success
import spray.json._
import akka.http.scaladsl.server.Directives._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.http.highlevelserver.PersonDb.{AddPerson, FindAllPeople, FindPerson}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, StatusCodes}
import akka.stream.ActorMaterializer
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure



case class Person(pin: Int, name: String)

trait PersonJsonProtocol extends DefaultJsonProtocol{
  implicit val personJson = jsonFormat2(Person)
}
object PersonDb{
  case object FindAllPeople
  case class FindPerson(id:Int)
  case class AddPerson(person:Person)
}

class PersonDb extends Actor with ActorLogging with PersonJsonProtocol {
  import PersonDb._
  var people: List[Person] = List(
    Person(1, "Alice"),
    Person(2,"Bob"),
    Person(3,"Charlie")
  )

  override def receive: Receive = {
    case FindAllPeople => {
      log.info(s"get all peoplle")
      sender() ! people}
    case FindPerson(id) => people.filter(p => p.pin==id)
    case AddPerson(person) =>
       people :+ person
  }
}

object HighLevelExercise extends App with PersonJsonProtocol {
  implicit val system = ActorSystem("HighLevelExercise")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher


  val peopleDb = system.actorOf(Props[GuitarDB], "PeopleActor2")

  def toHttpEntity(payload:String) = HttpEntity(ContentTypes.`application/json`,payload)

  implicit val timeout = Timeout(3 seconds)

  val personServerRoute = pathPrefix("api" / "people") {
    get {
      (path(IntNumber) | parameter('pin.as[Int])) { pinNumber: Int =>
        complete(
          (peopleDb ? FindPerson(pinNumber))
          .mapTo[List[Person]]
          .map(_.toJson.prettyPrint)
          .map(toHttpEntity)
        )
      }~
        pathEndOrSingleSlash {
          extractLog { (log:LoggingAdapter) =>
            log.info("routing starrt")
            complete(
              (peopleDb ? FindAllPeople)
                .mapTo[List[Person]]
                .map(a => a.toJson.prettyPrint)
                .map(toHttpEntity)
            )
          }
        }
    }~
      (post & pathEndOrSingleSlash & extractRequest & extractLog){ (request, log) =>
      val entity= request.entity
      val strictEntityFuture: Future[HttpEntity.Strict] = entity.toStrict(2 seconds)
      val personFuture: Future[Person] = strictEntityFuture.map(_.data.utf8String.parseJson.convertTo[Person])

        onComplete(personFuture){
          case Success(person) =>
            log.info(s"Got person $person")
            peopleDb ? AddPerson
            complete(StatusCodes.OK)
          case Failure(ex) =>
            failWith(ex)
        }
    }
  }
  Http().bindAndHandle(personServerRoute,"localhost",8080)
}
