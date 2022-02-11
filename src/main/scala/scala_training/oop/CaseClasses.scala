package scala_training.oop

object CaseClasses extends App{

  case class Person(name:String, age: Int)
  //1.class parameters are fields ex: name , age
  val jim = new Person("Jim",34)
  println(jim.age)

  //2.sensible toString
  println(jim.toString)
  // if I print object it propogate toString
  println(jim)

  //3. equals and hashcode implemented out of the box
  val jim2 = new Person("Jim", 34)
  println(jim2.hashCode())
  println(jim == jim2)
  //4.CC's have handy copy method
  val jim3 = jim.copy(age = 45)
  println(jim3)
  //5.CC's have companion object
  val thePerson = Person
  val mary = Person("Mary", 23)
  val joe = Person("Joe",30)
  println(joe)
  //6.CC's are serializable
  //Akka

  //7.CC's have extractor patterns = CC's can be used in PATTERN MATCHİNG
  case object UnitedKingdom{
    def name: String= "The UK of GB and NI"
  }
}
