package akka.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App{
  class SimpleActor extends Actor{
    override def receive: Receive = {
      case "Hi" => context.sender ! "hello there"
      case message:String => println(s"[${context.self}] have received a String $message")
      case number:Int => println(s"[simple actor] have received a Number $number")
      case SpecialMessge(content) => println(s"[simple actor] have received a SpecialMessage $content")
      case SendMessageToYourself(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi"
      case WirelessPhoneMessage(content, ref) => ref forward(content + "s" )
    }
  }
  val actorSystem = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simpleActor")
  simpleActor ! ""
  simpleActor ! 2
  simpleActor ! SpecialMessge("special")
  case class SpecialMessge(content:String)
  case class SendMessageToYourself(content:String)

  simpleActor ! SendMessageToYourself("I am an actor")
  //3.actors can reply to messages
  val alice = actorSystem.actorOf(Props[SimpleActor], "alice")
  val bob = actorSystem.actorOf(Props[SimpleActor], "bob")
  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)
  //4. dead letters
  alice ! "Hi"
  //5. forwarding message

  case class WirelessPhoneMessage(content:String, ref: ActorRef)
  alice ! WirelessPhoneMessage("Hi", bob)

}
