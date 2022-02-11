package scala_training.fp

object MapsAndTuples extends App{
  val phoneBook = Map("Jim" -> 555, "Tom" -> 222)
  println(phoneBook.filterKeys(x => x.startsWith("J")))
  val names = List("Bob","James","Angela","Mary","Daniel","Jim")
  println(names.groupBy(x => x.charAt(0)))
  val phoneBook2 = Map("Jim" -> 555, "JIM" -> 222)
  println(phoneBook2.map(p => p._1.toLowerCase -> p._2))

}
