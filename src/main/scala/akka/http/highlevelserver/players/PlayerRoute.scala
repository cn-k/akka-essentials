package akka.http.highlevelserver.players

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.highlevelserver.players.PlayerDbTyped._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, get, parameter, path, pathEndOrSingleSlash, pathPrefix, post, _}
import akka.util.Timeout
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._


//marshalling
trait PlayerJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  import PlayerDbTyped._

  implicit val playersFormat = jsonFormat3(Player)
}

class PlayerRoute(system: ActorSystem[_], playerRepo: ActorRef[PlayerDbTyped.PlayerRepo]) extends PlayerJsonProtocol {
  lazy val log = system.log
  implicit val executionContext = system.executionContext

  implicit val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration
  implicit val scheduler = system.scheduler

  val playersRoute =
    (pathPrefix("api" / "player")) {
      (post & pathEndOrSingleSlash & extractRequest & extractLog) { (request, log) =>
        entity(as[Player]) { player =>
          val operationPerformed: Future[Response] =
            playerRepo.ask(AddPlayer(player, _))
          onSuccess(operationPerformed) {
            case PlayerDbTyped.OK => complete("Job added")
            case PlayerDbTyped.KO(reason) => complete(StatusCodes.InternalServerError -> reason)
          }

        }
      }~
      delete {
        entity(as[Player]) { player =>
          complete(
            "" //(playerRepo ? RemovePlayer(player)).map(_ => StatusCodes.OK)
          )
        }
      }~
       get {
          path("class" / Segment) { charClass =>
            val players: Future[List[Player]] = playerRepo ? (replyTo => GetPlayersByClass(charClass, replyTo))
            complete(players)
          } ~
            (path(Segment) | parameter("nickname")) { nickname =>
              val players: Future[Option[Player]] = playerRepo ? (replyTo => GetPlayer(nickname, replyTo))
              complete(players)
            } ~
            pathEndOrSingleSlash {
              val players: Future[List[Player]] = playerRepo ? (replyTo => GetAllPlayers(replyTo))
              complete(players)
            }
        }
    }
}
