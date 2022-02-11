package akka.akka_stream

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration._

object IntegratingWithActors extends App {
  implicit val system = ActorSystem("GraphBasics")
  implicit val materializer = ActorMaterializer()

  class SimpleActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case s:String =>
        log.info(s"just received a string: $s")
        sender() ! s"$s $s"
      case n:Int =>
        log.info(s"just received a number: $n")
        sender() ! (n*2)
    }
  }
  val simpleActor = system.actorOf(Props[SimpleActor],"simpleActor")
  val numberSources = Source(1 to 10)
  //actor as a flow
  implicit val timeout = Timeout(2 seconds)
  val actorBasedFlow = Flow[Int].ask[Int](parallelism = 4)(simpleActor)

  //numberSources.via(actorBasedFlow).to(Sink.ignore).run()
  //numberSources.ask[Int](parallelism = 4)(simpleActor).to(Sink.ignore).run()//equvilant

  // Actor as a source
  val actorPoweredSource = Source.actorRef[Int](bufferSize = 10, overflowStrategy = OverflowStrategy.dropHead)
  val materializedActorRef = actorPoweredSource.to(Sink.foreach[Int](number => println(s"Actor powered flow got " +
    s"number: $number"))).run()
  materializedActorRef ! 10
  materializedActorRef ! akka.actor.Status.Success("complete")

  // Actor as a destination -> sink
  // - an init message
  // - an ack message to confirm the reception
  // - complete a message
  // - a function to generate a message in case stream throws an exception

  case object StreamInit
  case object StreamAck
  case object StreamComplete
  case class StreamFail(ex:Throwable)

  class DestinationActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case StreamInit =>
        log.info(s"Stream initiaized")
        sender() ! StreamAck
      case StreamComplete =>
        context.stop(self)
        log.info("stream complete")
      case StreamFail(ex) =>
        log.warning(s"Stream failed $ex")
      case message =>
        log.info(s"Message $message has come to its final resting point")
        sender() ! StreamAck
    }
  }
  val destinationActor = system.actorOf(Props[DestinationActor],"DestinationActor")
  val actorPoweredSink = Sink.actorRefWithAck[Int](
    destinationActor,
    onInitMessage = StreamInit,
    onCompleteMessage = StreamComplete,
    ackMessage = StreamAck,
    onFailureMessage = throwable => StreamFail(throwable) //optional
  )
  Source(1 to 10).to(actorPoweredSink)
}
