package akka.actors

import Exercise.CounterActor.{Decrement, Increment, Print}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object Exercise extends App{

  val actorSystem = ActorSystem("firstActorSystem") //dont use space

  //DOMAIN of the counter
  object CounterActor{
    case object Increment
    case object Decrement
    case object Print
  }

  var count = 1
  class CounterActor extends Actor{
    import CounterActor._
    override def receive: Receive = countReceive(0)
    def countReceive(currentCount:Int): Receive = {
      case Increment => context.become(countReceive(currentCount+1))
      case Decrement => context.become(countReceive(currentCount-1))
      case Print => println(s"[count receive(${currentCount})]  my current count is ${currentCount}")
    }
  }

  //val counter = actorSystem.actorOf(Props[CounterActor], "myCounter")
  //(1 to 5).foreach(_ => counter ! Increment)
  //(1 to 3).foreach(_ => counter ! Decrement)
  //counter ! Print

  // simple voting system


  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])
  //handle messages for voting
  //if I send Vote message to Citizen I will mark Citizen as having voted for this candidate

  class Citizen extends Actor{
    //var candidate:Option[String] = None
    override def receive: Receive = {
      //case Vote(c) => candidate=Some(c)
      //case VoteStatusRequest => sender() !  VoteStatusReply(candidate)
      case Vote(candidate) => context.become(voter(candidate))
      case VoteStatusRequest => sender() !  VoteStatusReply(None)
    }
    def voter(candidate:String):Receive = {
      case VoteStatusRequest => sender() !  VoteStatusReply(Some(candidate))
    }
  }
  //Vote aggregrator send message to citizen who you are voted for
  //ask each citizen VoteStatusRequest
  //and each citizen will reply VoteStatusReply
  case class AgrregateVotes(citizens:Set[ActorRef])

  class VoteAggregator extends Actor{
    //var stillWaiting  :Set[ActorRef] = Set()
    //var currentStats : Map[String, Int]=Map()

    override def receive: Receive = awaitingCommand
    def awaitingCommand:Receive = {
      case AgrregateVotes(citizens) => {
        citizens.foreach(c => c ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens,Map()))
      }
    }
    def awaitingStatuses(stillWaiting  :Set[ActorRef] = Set(), currentStats : Map[String, Int]=Map()):Receive ={
      case VoteStatusReply(None) => sender() ! VoteStatusRequest
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate,0)
        val newStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if(newStillWaiting.isEmpty){
          println(s"[agrregrator] poll stats: $newStats")
        }
        else{
          context.become(awaitingStatuses(newStillWaiting,newStats))
        }
    }
  }
  val alice = actorSystem.actorOf(Props[Citizen], "alice")
  val bob = actorSystem.actorOf(Props[Citizen], "bob")
  val charlie = actorSystem.actorOf(Props[Citizen], "charlie")
  val daniel = actorSystem.actorOf(Props[Citizen], "daniel")

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")
  val aggregator = actorSystem.actorOf(Props[VoteAggregator])
  aggregator ! AgrregateVotes(Set(alice, bob, charlie, daniel))
}
