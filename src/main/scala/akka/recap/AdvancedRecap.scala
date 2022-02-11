package akka.recap

import scala.concurrent.Future

object AdvancedRecap extends App{

  val partilFuncion: PartialFunction[Int,Int]={
    case 1 => 42
    case 2 =>55
    case 5 => 100
  }
  val pf = (x:Int) => x match{
    case 1 => 42
    case 2 =>55
    case _ => 100
  }
  println(pf(1))

  val function: (Int => Int)= pf
  println(function(12))

  val modifiedList=List(1,2,3).map{
    case 1 => 42
    case _ => 0
  }
  println(modifiedList)

  val lifted = partilFuncion.lift //total function Int => Option[Int]
  println(lifted(2)) //= Some(55)
  println(lifted(5000)) //None

  val pfChain = partilFuncion.orElse[Int,Int] {
    case 60 => 9000
  }
  println(pfChain(2)) //55
  println(pfChain(60))//9000

  //type aliases
  type ReceiveFunction = PartialFunction[Int,Unit]
  def receive:ReceiveFunction= {
    case 1 => println("hello")
    case _ => println("confused...")
    }
    //implicits
    implicit val timeout = 3000
    def setTimeout(f:()=>Unit)()(implicit timeout:Int) = f()
    setTimeout(()=>println("timeout"))//extra parameter list omitted

    //implicit conversions
    //1. implicit defs
    case class Person(name:String){
      def greet = println(s"Hi my name is $name")
    }
    implicit def fromStringToPerson(s:String):Person=Person(s)
    "Peter".greet
    //2. implicit classes
    implicit class Dog(name: String){
      def bark = println("bark")
    }
    "Lassie".bark //new Dog("Lassie").bark Automatically done by the compiler

    //Oganize
    //local scope
    implicit val ordering:Ordering[Int] = Ordering.fromLessThan(_ > _ )
    println(List(1,2,3).sorted)

    //imported scope
    import scala.concurrent.ExecutionContext.Implicits.global
    val future = Future{
      println("hello future")
    }

    //companion object of the types included in the call
    object Person{
      implicit val personOrdering:Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name)<0)
    }
    println(List(Person("Bob"),Person("Alice")).sorted)
}
