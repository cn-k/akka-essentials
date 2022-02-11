package akka.actors

import CounterActorExmple.BankAccount.{Deposit, Statement, TransactionFailure, TransactionSuccess, WithDraw}
import CounterActorExmple.CounterActor.{Decrement, Increment, Print}
import CounterActorExmple.Person.LiveTheLife
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object CounterActorExmple  extends App{
  val actorSystem = ActorSystem("firstActorSystem") //dont use space

  //DOMAIN of the counter
  object CounterActor{
    case object Increment
    case object Decrement
    case object Print
  }

  var count = 1
  class CounterActor extends Actor{
    import CounterActor._
    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"[CounterActor] my current count is $count")
    }
  }
  val counter = actorSystem.actorOf(Props[CounterActor], "myCounter")
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  object BankAccount{
    case class Deposit(amount:Int)
    case class WithDraw(amount:Int)
    case object Statement

    case class TransactionSuccess(reason:String)
    case class TransactionFailure(reason:String)
  }
  class BankAccount extends Actor{
    var funds = 0

    override def receive: Receive = {
      case Deposit(amount: Int) => {
        if(amount<=0) sender() ! TransactionFailure("invalid deposit amount")
        else {
          funds += amount
          sender() ! TransactionSuccess(s"succesfully deposited $amount")
        }
      }
      case WithDraw(amount: Int) => {
        if (amount <=0) sender() ! TransactionFailure("invalid deposit amount")
        else if (amount > funds) sender() ! TransactionFailure("Insufficient funds")
        else {
          funds -= amount
          sender() ! TransactionSuccess(s"succesfully withdrew $amount")
        }
      }
      case Statement => sender() ! s"Your balance is $funds"
    }
  }
  object Person{
    case class LiveTheLife(ref: ActorRef)
  }
  class Person extends Actor{
    import Person._
    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(10000)
        account ! WithDraw(90000)
        account ! WithDraw(500)
        account ! Statement
      case message => println(message.toString)
    }
  }
  val account = actorSystem.actorOf(Props[BankAccount],"bankAccount")
  val person = actorSystem.actorOf(Props[Person],"billionare")
  person ! LiveTheLife(account)
}
