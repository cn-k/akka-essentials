package akka.actors

import scala_training.oop.CaseClasses.Person
import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{
  //actor system
  val actorSystem = ActorSystem("firstActorSystem") //dont use space
  println(actorSystem.name)
  //create actors
  class WordCountActor extends Actor{
    var totalWords = 0
    override def receive:PartialFunction[Any,Unit] = {
      case message:String =>
        println(s"[word counter] I have received: $message")
        totalWords += message.split(" ").length
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }
  //instatiate our actor
  val wordCounter = actorSystem.actorOf(Props[WordCountActor],"wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor],"anotherWordCounter")

  //communicate
  wordCounter ! "I am learning Akka"
  wordCounter ! "I am learning Akka2"
  anotherWordCounter ! "different message"
  //asynchronous
  wordCounter
  object Person{
    def props(name: String): Props =Props(new Person(name))
  }
  class Person(name:String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"hi my name is $name")
      case _ =>
    }
  }
  val person = actorSystem.actorOf(Props (new Person("Bob")))
  val person2 = actorSystem.actorOf(Person.props("josh"))
  person ! "hi"
  person2 ! "hi"
}
