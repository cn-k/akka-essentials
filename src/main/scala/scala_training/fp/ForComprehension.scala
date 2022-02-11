package scala_training.fp

object ForComprehension extends App{
  val list1 = List(1,2,3)
  var list2 = List(4,5,6)

    val res = for{
    x <- list1 if x % 2==0
    y <- list2
  } yield x +"-"+y
  println(res)
  for (x <- list1)println(x)
}
