package scala_training.fp

object WhatsaFunction extends App{

  //functions are first level arguments
  //problem oop
  val doubler = new Function[Int,Int]{
    override def apply(element:Int):Int = element * 2
   }

  val adder: ((Int,Int) => Int) = new Function2[Int,Int,Int]{
    override def apply(a:Int,b:Int):Int =a+b
  }
  println(doubler(2))
  val concatanate = new Function2[String,String,String]{
    override def apply(v1: String, v2: String): String = v1 + v2
  }

}

trait Function[A,B]{
  def apply(element:A): B
}
