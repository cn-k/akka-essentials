package akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, PoisonPill}

import scala.concurrent.duration._
object TimersSchedulers extends App{

  class SelfClosingActor extends Actor with ActorLogging{
    var schedule = createTimeoutWindow()
    val system = ActorSystem("SchedulersTimersDemo")
    import system.dispatcher
    override def receive: Receive = {
      case "timeout" =>
        log.info("stopping myself")
        context.stop(self)
      case message =>
        log.info(s"received $message , staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow()

    }
    def createTimeoutWindow():Cancellable = {
      context.system.scheduler.scheduleOnce(1 second){
        self ! "timeout"
      }
    }
  }
}
