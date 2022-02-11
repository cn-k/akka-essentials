package scala_training.fp

object HOFsCurries extends App{

  def nTimes(f: Int => Int, n:Int, x:Int): Int = {
    if(n<=0)x
    else nTimes(f, n-1, f(x))
  }
  val plusOne = (x:Int) => x+1
  println(nTimes(plusOne,3,4))

  def nTimesBetter (f:Int => Int, n:Int): (Int => Int) ={
    if (n<=0) (x:Int)=>x
    else (x:Int) => nTimesBetter(f,n-1)(f(x))
  }
  val superAdder = (x:Int) => (y:Int) => x+y
  val add = superAdder(3)
  println(add(14))
  println(superAdder(3)(14))
  def curriedFormatter(c:String)(x:Double): String = c.format(x)
  val standardFormat :(Double => String)=curriedFormatter("%4.2f")
  val preciseFormat :(Double => String)=curriedFormatter("%10.8f")
  println(standardFormat(Math.PI))
  println(preciseFormat(Math.PI))
}