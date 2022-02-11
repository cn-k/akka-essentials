package akka.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {

  //Actors can create other actors
  object Parent{
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor{
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) => {
        println(s"${self.path} creating child")
        //create a new actor right here
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
      }
    }

    def withChild(childRef: ActorRef): Receive ={
      case TellChild(message) => childRef forward message
    }
  }

  class Child extends Actor{
    override def receive: Receive = {
      case message => println(s"${self.path} I got $message")
    }
  }
  import Parent._
  val system = ActorSystem("ParentChildDemoo")
  val parent = system.actorOf(Props[Parent],"parent")
  parent ! CreateChild
  parent ! TellChild

  /*
  Guardian actors = top level
  - /system = system guardian -> collecting logs and do actor system staff
  - /user  = user leve guardian -> every actor we create is child of user
  - /root = manages system and user
   */

  //Actor selection
  //val childSelection = system.actorSelection("/user/parent/child")
  //childSelection ! "I found you"
}
