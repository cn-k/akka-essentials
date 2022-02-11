package akka.akka_stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusion extends App{
  implicit val system = ActorSystem("OperatorFusion")
  implicit val materializer = ActorMaterializer()

  val simpleSource = Source(1 to 1000)
  val simpleFlow = Flow[Int].map(_ + 1)
  val simpleFlow2 = Flow[Int].map(_ * 10)
  val simpleSink = Sink.foreach[Int](println)
  //it runs on same actor
  simpleSource.via(simpleFlow).via(simpleFlow2).to(simpleSink).run()
  //complex flox
  val complexFlow = Flow[Int].map{x =>
    Thread.sleep(1000)
    x + 1
  }
  val complexFlow2 = Flow[Int].map{x =>
    Thread.sleep(1000)
    x *10
  }
  //simpleSource.via(complexFlow).via(complexFlow2).to(simpleSink).run()
  //async boundary
  /*
  simpleSource.via(simpleFlow).async //runs on one actor
    .via(complexFlow2).async //run on another actor
    .to(simpleSink).run()
   */
  //oredering guarantees

  Source(1 to 3).map(element =>{
      println(s"Flow A: $element")
      element})
    .async
    .map(element =>{
      println(s"Flow B: $element")
      element})
    .async
    .map(element =>{
      println(s"Flow C: $element")
      element})
    .async
    .runWith(Sink.ignore)



}
