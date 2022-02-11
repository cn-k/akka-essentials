package scala_training.fp

object AnanymousFunctions extends App{
  val doubler = (x:Int)=> x*2
  //val doubler:Int => Int = x => x*2
  //multiple parameters in lamda
  val adder = (x:Int, y:Int) => x+y
  //no paramater
  val justDoSomething = () => 3
  //careful
  println(justDoSomething)//function itself
  println(justDoSomething())//function call

  //MOAR syntatic sugar
  val niceIncrementer:Int => Int = _ +1 //equivalent to x => x+1
  val niceAdder: (Int, Int) => Int  = _ + _ //equivalent to (a,b) => a + b -- dont forget to use types of parameters

  val superAdd = (x:Int) => (y:Int) => x+y //lamda curried function = partial function

  println(superAdd(3)(4))


}
