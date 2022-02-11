package akka.http.highlevelserver

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.duration._


case class Player(nickname: String, characterClass: String, level: Int)

object GameAreaMap {

  case object GetAllPlayers

  case class GetPlayer(nickname: String)

  case class GetPlayersByClass(characterClass: String)

  case class AddPlayer(player: Player)

  case class RemovePlayer(player: Player)

  case object OperationSuccess

}

class GameAreaMap extends Actor with ActorLogging {

  import akka.http.highlevelserver.GameAreaMap._

  var players = Map[String, Player]()

  override def receive: Receive = {
    case GetAllPlayers =>
      log.info("Getting all playyers")
      sender() ! players.values.toList
    case GetPlayer(nickname) =>
      log.info(s"Getting player by nickname $nickname")
      sender() ! players.get(nickname)
    case GetPlayersByClass(characterClass) =>
      log.info(s"Getting all player with the character class $characterClass")
      sender() ! players.values.toList.filter(_.characterClass == characterClass)
    case AddPlayer(player) =>
      log.info(s"trying to add player $player")
      players = players + (player.nickname -> player)
      sender() ! OperationSuccess
    case RemovePlayer(player) =>
      log.info(s"trying to delete player $player")
      players = players - player.nickname
      sender() ! OperationSuccess
  }
}

//marshalling
trait PlayerJsonProtocol extends DefaultJsonProtocol {
  implicit val playerFormat = jsonFormat3(Player)
}


object MarshallingJson extends App with PlayerJsonProtocol with SprayJsonSupport{

  import akka.http.highlevelserver.GameAreaMap._

  implicit val system = ActorSystem("MarshallingJson")
  implicit val materializer = ActorMaterializer

  import system.dispatcher


  val myGameMap = system.actorOf(Props[GameAreaMap], "MyGameMap")
  val playersList = List(
    Player("Martin Kills u", "Warrior", 70),
    Player("Rolan", "Elf", 67),
    Player("cc", "Wizard", 50)
  )

  playersList.foreach { player =>
    myGameMap ! AddPlayer(player)
  }

  def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)

  implicit val timeout = Timeout(2 seconds)

  val playersRoute =
    (pathPrefix("api" / "player")) {
      get {
        path("class" / Segment) { charClass =>
          complete(
            (myGameMap ? GetPlayersByClass(charClass))
              .mapTo[List[Player]]
              .map(_.toJson.prettyPrint)
              .map(toHttpEntity)
          )
        }
        (path(Segment) | parameter("nickname")){nickname =>
            complete(
              (myGameMap ? GetPlayer(nickname))
                .mapTo[Option[Player]]
            )
          }~
          pathEndOrSingleSlash {
            complete(
              (myGameMap ? GetAllPlayers)
                .mapTo[List[Player]]
            )
          }
        } ~
      post{
        entity(as[Player]){player =>
          complete(
            (myGameMap ? AddPlayer(player)).map(_ => StatusCodes.OK)
          )
        }
      }~
      delete{
        entity(as[Player]){player =>
          complete(
            (myGameMap ? RemovePlayer(player)).map(_ => StatusCodes.OK)
          )
        }
      }

      }
  Http().bindAndHandle(playersRoute, "localhost", 8080)
}
