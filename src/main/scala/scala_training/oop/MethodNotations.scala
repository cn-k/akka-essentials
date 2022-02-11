package scala_training.oop

object MethodNotations extends App{

  class Person(val name: String, favoriteMovie: String, val age: Int){
    def likes( movie: String):Boolean= movie == favoriteMovie
    def hangOutWith(person: Person):String = s"${this.name} is hanging out with ${person.name}"
    def +(person: Person):String = s"${this.name} is hanging out with ${person.name}"
    def unary_! : String = s"$name what the hack"
    def isAlive :Boolean = true
    def apply(): String =s"hi my name is $name and I like $favoriteMovie"
    def + (nickName:String): Person = new Person (this.name + " " + nickName, this.favoriteMovie, this.age)
    def unary_+ = new Person(this.name,this.favoriteMovie, this.age+1)
    def learn(item: String= "Scala"): String = s"$name learns $item"
    def learnScala:String = learn()
    def apply(n:Int):String = s"Mary watched movie $n times"
  }

  val mary = new Person("Mary","Inception", 20)
  println(mary.likes("Inception"))
  //Infix notation or operator notation (syntactic sugar)
  println(mary likes "Inception")
  val tom = new Person("Tom","Shutter Ireland", 22)
  println(mary hangOutWith tom)
  //ALL OPERATORS ARE METHODS

  //prefix notations
  val x = -1
  val y = 1.unary_-
  //unary prefix only works with - + ~ !

  println(!mary)
  //postfix notation

  println(mary.isAlive)
  println(mary isAlive)

  //apply -> special property in scala
  //below code are same
  println(mary.apply())
  println(mary())


  println((mary + "nickname").name)
  println((+mary ).age)

  println(mary learnScala)
  println(mary.apply(2))

}
