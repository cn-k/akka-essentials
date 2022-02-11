package scala_training.oop

object OOBasics extends App{

  val person1 = new Person("cenk",27)
  println(person1.name)
  person1.greet("Daniel")
  person1.greet()
}
//constructor
class Person(val name:String, val age:Int=0){
  //body
  //var and val declarations are fields
  val x = 2
  println(3)
  def greet(name: String): Unit ={
    println(s" ${this.name} says , hi $name")
  }
  //overloading
  def greet(): Unit ={
    println(s" hi I am $name ")
  }

  //multiple constructors
  //def this(name:String)=this(name,0)
  def this() = this("deneme")
}
