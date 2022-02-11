package scala_training.oop

object AbstractDataTypes extends App{
  //abstract
  abstract class Animal{
    val creatureType: String = "wild"
    def eat: Unit
  }
  class Dog extends Animal{
    override val creatureType: String = "Canine"
    def eat: Unit = println("crunch crunch")
  }
  trait Carnivore {
    def eat(animal: Animal):Unit
  }
  trait ColdBlood

  class Crocodile extends Animal with Carnivore with ColdBlood  {
    override val creatureType: String = "croc"
    def eat: Unit = println("num")
    def eat(animal:Animal) = s"Im a croc and I am eating ${animal.creatureType}"
  }
  val dog = new Dog
  val croc = new Crocodile
  croc.eat(dog)
  // traits vs abstract classes
  //traits do not have constructor parameters
  //multiple traits may be inhereted by the same class, but single class inheritance
  //traits = behavior, abstract class="thing "
}
