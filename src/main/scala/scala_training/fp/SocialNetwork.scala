package scala_training.fp

object SocialNetwork extends App{
  def add(network: Map[String,Set[String]],person: String): Map[String,Set[String]] ={
  network + (person -> Set())
  }
  def friend(network: Map[String,Set[String]],a: String, b:String): Map[String,Set[String]]={
    val friendsA=network(a)
    val friendsB=network(b)
    network + (a -> (friendsA + b)) + (b -> (friendsB + a))
  }
  def unfriend(network: Map[String,Set[String]],a: String, b:String): Map[String,Set[String]]={
    val friendsA=network(a)
    val friendsB=network(b)
    network + (a -> (friendsA - b)) + (b -> (friendsB - a))
  }
  def remove(network: Map[String,Set[String]],person: String): Map[String,Set[String]] ={

    network.map(n => n._1 -> n._2.filter(l => l!=person)).filterKeys(n => n!=person)
  }
  val empty: Map[String, Set[String]]= Map()
  val network = add(add(add(empty, "Mary"),"Bob"),"Josh")
  val n2= friend(network,"Mary", "Bob")
  val n3 = friend(n2,"Mary", "Josh")
  val n4 = friend(n3,"Bob", "Josh")

  println(remove(n4,"Bob"))
}
