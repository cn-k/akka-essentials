package scala_training.fp

import scala.util.Random

object PatternMatching extends App{
  val random = new Random()
  val x = random.nextInt(10)
  val description = x match{
    case 1 => "One"
    case 2 => "Two"
    case 3 => "Three"
    case _  => "nothing"
  }
  println(x)
  println(description)
  //Decompose values
  case class Person(name: String, age: Int)
  val bob = Person("Bob", 33)
  //if Bob is person it returns bob is person else returns I dont know who am I
  val greeting = bob match {
    case Person(n,a) if a <21 => "Bob is a person" //if bob is a person and his age smaller then 21
    case _ => "I don know Who am I"
  }
  println(greeting)
}
