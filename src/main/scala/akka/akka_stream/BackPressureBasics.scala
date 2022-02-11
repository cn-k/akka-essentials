package akka.akka_stream

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}

object BackPressureBasics extends App {
  implicit val system = ActorSystem("BackPressureBasics")
  implicit val materializer = ActorMaterializer()

  val fastSource = Source(1 to 1000)
  val slowSink = Sink.foreach[Int]{x =>
    Thread.sleep(1000)
    println(s"Sink $x ")
  }
  //not backpressure
  //fastSource.to(slowSink).run()

  //backpressure
  //fastSource.async.to(slowSink).run()
  val simpleFlow = Flow[Int].map{ x =>
    println(s"Incoming: $x")
    x + 1
  }
  //fastSource.async.via(simpleFlow).async.to(slowSink).run()
  /*
  reactions to backpressure(in order):
  - try to slow down if possible
  - buffer element until there is more demand
  - dropdown element from buffer if it overflows
  - tear down/kill the whole stream(failure)
   */
  val bufferedFlow = simpleFlow.buffer(10, overflowStrategy = OverflowStrategy.dropHead)
  //fastSource.async.via(bufferedFlow).async.to(slowSink).run()
/*
  1-16 :nobody is backpressured
  17-26: flow will buffer, flow will start dropping at the next element
  26-100: flow will always drop the oldest element
  => 991-1000 => 992*1000 => sink
 */
  /*
  overflow strategies
  - drop head: drop the oldest element
  - drop tail: drop the newest element
  - drop new: exact element to be added: keeps the buffer
  - drop the entire buffer
  - backpressure signal
  - fail
   */

  //throttling
  import scala.concurrent.duration._
  fastSource.throttle(2, 1 second).runWith(Sink.foreach(println))
}
