package akka.recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MultiThreadingRecap extends App{
  //creating threads on jvm
  val aThread = new Thread(()=> println("running parallel"))
  aThread.start()
  aThread.join()
  //@volatile do same as syncronized but it works for just decimal types like Int
  class BankAccount(@volatile private var amount:Int){
    override def toString:String = "" + amount
    //it is not thread safe
    def withdraw(money:Int) = this.amount -= money
    def safeWithdraw(money:Int)=this.synchronized{this.amount -= money}
  }
  //inter thread communication on JVM
  //wait - notify mechanism

  //Scala Future
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future{
    42
  }
  //callbacks
  future.onComplete{
    case Success(42) => "I found the meaning of life"
    case Failure(_) => "FAILED"
  }
  val aProcessedFuture = future.map(_ + 1)
  val aFlatFuture = future.flatMap(value => Future(value + 2))
  val aFilteredFuture = future.filter(f => f %2 ==0)
}
