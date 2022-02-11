package scala_training.oop

object Objects extends App{

  //static=class level functionality
  object Person{
    val N_EYES=2
    def canFly: Boolean = false
    def apply(father: Person ,mother: Person):Person = new Person("Bobbie")
  }
  //Instance level functionality
  class Person (val name:String){

  }
  //if there is same name of object and class in the same scope its called
  //COMPANIONS
  //class Person + object Person = COMPANION
  println(Person.N_EYES)
  println(Person.canFly)

  //Scala object = SINGLETON INSTANCE

  val mary= new Person("mary")
  val john = new Person("john")
  println(mary==john)
  val bobie = Person(john,mary)

}
