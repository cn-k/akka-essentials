package akka.akka_stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object FirstPrinciples extends App {
  implicit val system = ActorSystem("FirstPrinciples")
  implicit val materializer = ActorMaterializer()

  //sources
  val source = Source(1 to 10)
  //sinks
  val sink = Sink.foreach[Int](println)
  val graph = source to sink
  //graph.run()
  //flows transform element
  val flow = Flow[Int].map(_+1)
  val sourceWithFlow: Source[Int, NotUsed] = source via flow
  //(sourceWithFlow to sink).run()
  val names = List("Alice","Bob","Charlie","David","Martin","Akkakaka")
  val nameSource = Source(names)
  val nameFlow = Flow[String].filter(_.length > 5)
  val limitFlow = Flow[String].take(2)
  val nameSink = Sink.foreach[String](println)
  nameSource.via(nameFlow).via(limitFlow).to(nameSink).run()
  nameSource.filter(_.length >5).take(2).runForeach(println) // same with above code

  val mapSource= Source(1 to 10).map(_ * 2) // Source(1 to 10).via(Flow[Int].map(_ * 2))
  mapSource.runForeach(println) //mapSource.to(Sink.foreach[Int](printkn)).run()

}
