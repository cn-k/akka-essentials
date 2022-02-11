package akka.actors

import ChangingActorBehavior.Mom.MomStart
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehavior extends App{

  object Kid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class Kid extends Actor {
    import Kid._
    import Mom._
    //internal state of the kid
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if(state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  class StatelessKid extends Actor{
    import Kid._
    import Mom._
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender ! KidAccept
    }
    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
      case Food(CHOCOLATE) => context.become(happyReceive)
      case Ask(_) => sender ! KidReject
    }
  }
  object Mom{
    case class MomStart(kidRef : ActorRef)
    case class Food(food: String)
    case class Ask(message: String)//do u want to play ?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class Mom extends Actor{
    import Mom._
    import Kid._
    override def receive: Receive = {
      case MomStart(kidRef)=>
        //test our interaction
      kidRef ! Food(VEGETABLE)
      kidRef ! Ask("do u want to play?")
      case KidAccept => println("yey my kid is happy")
      case KidReject => println("My kid is sad but at least he's healty")
    }
  }
  val system = ActorSystem("changingActorBehaviorDemo")
  val mom = system.actorOf(Props[Mom],"momActor")
  val kid = system.actorOf(Props[Kid], "kidActor")
  val statelessKid = system.actorOf(Props[StatelessKid], "statelessKidActor")

  //mom ! MomStart(kid)
  mom ! MomStart(statelessKid)
}
