package akka.akka_stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Sink
import akka.stream.scaladsl.{Flow, Source}


object AkkaStreamsRecap extends App{
  implicit val system = ActorSystem("AkkaStreamsRecap")
  implicit val materailizer = ActorMaterializer

  val source = Source(1 to 100)
  val sink = Sink.foreach[Int](println)
  val flow = Flow[Int].map(x => x+1)
  val runnableGraph = source.via(flow).to(sink)
  val simpleMaterializedValue = runnableGraph.run()

}
