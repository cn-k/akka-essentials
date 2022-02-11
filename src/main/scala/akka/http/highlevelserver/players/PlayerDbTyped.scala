package akka.http.highlevelserver.players

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors


object PlayerDbTyped {

  case class Player(nickname: String, characterClass: String, level: Int)

  trait PlayerRepo

  case class GetAllPlayers(replyTo: ActorRef[List[Player]]) extends PlayerRepo

  case class GetPlayer(nickname: String, replyTo: ActorRef[Option[Player]]) extends PlayerRepo

  case class GetPlayersByClass(characterClass: String, replyTo: ActorRef[List[Player]]) extends PlayerRepo

  case class AddPlayer(player: Player, replyTo: ActorRef[Response]) extends PlayerRepo

  case class RemovePlayer(player: Player, replyTo: ActorRef[Response]) extends PlayerRepo


  sealed trait Response
  case object OK extends Response
  final case class KO(reason: String) extends Response


  def apply(players: Map[String, PlayerDbTyped.Player]): Behavior[PlayerRepo] =
    Behaviors.receiveMessage {
      case GetAllPlayers(replyTo) =>
        println("Getting all playyers")
        replyTo ! players.values.toList
        Behaviors.same
      case GetPlayer(nickname, replyTo) =>
        println(s"Getting player by nickname $nickname")
        replyTo ! players.get(nickname)
        Behaviors.same
      case GetPlayersByClass(characterClass,replyTo) =>
        println(s"Getting all player with the character class $characterClass")
        replyTo ! players.values.toList.filter(_.characterClass == characterClass)
        Behaviors.same
      case AddPlayer(player,replyTo) =>
        replyTo ! OK
        PlayerDbTyped(players + (player.nickname -> player))
      case RemovePlayer(player,replyTo) =>
        println(s"trying to del ete player $player")
        replyTo ! OK
        PlayerDbTyped(players = players - player.nickname)
    }

}
