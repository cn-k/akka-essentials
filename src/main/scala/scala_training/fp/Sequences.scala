package scala_training.fp

import scala.util.Random

object Sequences extends App{
  val aSequence = Seq(1,4,3,2)
  println(aSequence)
  println(aSequence.reverse)
  println(aSequence(2))
  println(aSequence ++ Seq(6,9,7))
  println(aSequence.sorted)
  //lists are seq
  //ranges are also seq

  val aRange : Seq[Int]= 1 to 10
  val anotherRange : Seq[Int]= 1 until 10
  aRange.foreach(println)
  anotherRange.foreach(println)
  (1 to 10).foreach(x => println(s"hello $x"))
  //lists
  val list = List(1,2,3)
  val prependedList = 42 +: list :+ 29
  prependedList.foreach(println)
  val apples5 = List.fill(5)("apple")
  println(apples5)
  println(list.mkString("||"))

  //Arrays
  val numbers = Array(1,2,3,4)
  val threeElements =Array.ofDim[Int](3)
  threeElements.foreach(println)
  //mutation
  numbers(2)=0
  println(numbers.mkString(" "))
  val numbersSeq: Seq[Int] = numbers //implicit conversion
  println(numbersSeq)

  val maxRuns = 1000
  val maxCapacity = 1000000
  def getWriteTime(collection: Seq[Int]):Double = {
  val r = new Random
  val times: Seq[Long] = for(
      it <- 1 to  maxRuns
    )yield{
      val currentTime = System.nanoTime()
      collection.updated(r.nextInt(maxCapacity),0)
      System.nanoTime() - currentTime
    }
    times.sum * 1.0 / maxRuns
  }
  val numbersList = (1 to maxCapacity).toList
  val numbersVector = (1 to maxCapacity).toVector
  println(getWriteTime(numbersList).formatted("%10.12f"))
  println(getWriteTime(numbersVector))

}
