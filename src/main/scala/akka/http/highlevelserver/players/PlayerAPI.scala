package akka.http.highlevelserver.players

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, PostStop}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.util.{Failure, Success}

object PlayerAPI {
  sealed trait Message

  private final case class StartFailed(cause: Throwable) extends Message

  private final case class Started(binding: ServerBinding) extends Message

  case object Stop extends Message

  def main(args: Array[String]): Unit = {
    val playerList: Map[String, PlayerDbTyped.Player] = Map(
      ("ceku" -> PlayerDbTyped.Player("ceku", "Elf", 12)),
      ("cc" -> PlayerDbTyped.Player("cc", "Dwarew", 12)),
      ("jeyjey" -> PlayerDbTyped.Player("Man", "Elf", 12))
    )
    val system: ActorSystem[Message] =
      ActorSystem(PlayerAPI("localhost", 8080, playerList), "playerServer")
  }

    def apply(host: String, port: Int, players: Map[String, PlayerDbTyped.Player]): Behavior[Message] = Behaviors
      .setup {
        ctx =>
          implicit val system = ctx.system
          val productRouteRef = ctx.spawn(PlayerDbTyped(players), "playerDbTypedActor")
          val routes = new PlayerRoute(system, productRouteRef)
          val serverBinding = Http().newServerAt(host, port).bind(routes.playersRoute)

          ctx.pipeToSelf(serverBinding) {
            case Success(binding) => Started(binding)
            case Failure(ex) => StartFailed(ex)
          }

          def running(binding: ServerBinding): Behavior[Message] =
            Behaviors.receiveMessagePartial[Message] {
              case Stop =>
                ctx.log.info(
                  "Stopping server http://{}:{}/",
                  binding.localAddress.getHostString,
                  binding.localAddress.getPort)
                Behaviors.stopped
            }.receiveSignal {
              case (_, PostStop) =>
                binding.unbind()
                Behaviors.same
            }

          def starting(wasStopped: Boolean): Behaviors.Receive[Message] =
            Behaviors.receiveMessage[Message] {
              case StartFailed(cause) =>
                throw new RuntimeException("Server failed to start", cause)
              case Started(binding) =>
                ctx.log.info(
                  "Server online at http://{}:{}/",
                  binding.localAddress.getHostString,
                  binding.localAddress.getPort)
                if (wasStopped) ctx.self ! Stop
                running(binding)
              case Stop =>
                // we got a stop message but haven't completed starting yet,
                // we cannot stop until starting has completed
                starting(wasStopped = true)
            }

          starting(wasStopped = false)
      }

}
