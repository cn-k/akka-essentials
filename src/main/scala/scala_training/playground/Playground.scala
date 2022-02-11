package scala_training.playground

import akka.actor.ActorSystem

object Playground extends App{

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
