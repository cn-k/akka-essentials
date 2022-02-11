package scala_training.oop

object Inheritance extends App{

  class Animal{
    val creatureType = "wild"
    def eat = println("nomnom")
  }
  class Cat extends Animal{
    def crunch = {
    eat
    println("crunch crunch")
    }
  }

  val cat = new Cat
  cat.crunch

  //constructors
  //u have to pass the constructor arguments
  class Person(name: String, age:Int){
    def this (name: String) = this(name,0)
  }

  class Adult (name: String, age: Int, idCard: Int) extends Person(name,age)
  class Adult2 (name: String, age: Int, idCard: Int) extends Person(name)
  //overriding
/*
  class Dog2 extends Animal{
    override val creatureType: String = "domestic"
    override def eat: Unit = println("crunch crunch")
  }
 */
  class Dog(override val creatureType: String) extends Animal{
    override def eat: Unit = {
      //super
      super.eat
      println("crunch crunch")
    }
  }
  val dog = new Dog("K9")
  dog.eat
  println(dog.creatureType)
  //type subtitution polymorphism
  val unknownAnimal: Animal = new Dog("K9")
  unknownAnimal.eat
  //if class final u cannot extends the class
  //sealed class = extend classes only THIS FILE, prevent exntension in other files
  //if u try to extend the class from certain classes use these classes in the same file and use sealed for parent class

}
