package scala_training.oop

object Generics extends App{

  class MyList[A]{
    // use type A
  }
  //generic values
  class MyMap[K,V]
  //generic list
  val listOfIntegers = new MyList[Int]
  val listOfStrings = new MyList[String]

  val kvList = new MyMap[Int,String]

  //generic methods
  object MyList{
    def empty[A]: MyList[A] = ???
  }
  //variance problem
  class Animal
  class Cat extends Animal
  class Dog extends Animal
  //


}
