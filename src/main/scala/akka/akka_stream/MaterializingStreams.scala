package akka.akka_stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object  MaterializingStreams extends App{

  implicit val system = ActorSystem("MaterializingStreams")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val source = Source(1 to 10)
  val sink = Sink.reduce[Int]((a,b) => a + b)
  /*
  val sumFuture: Future[Int] = source.runWith(sink)
  sumFuture.onComplete{
    case Success(value) => println(s"the sum of the all element: $value")
    case Failure(exception) => println(s"exception: $exception")
  }
   */
  val simpleSource = Source(1 to 10)
  val simpleFlow = Flow[Int].map(_+1)
  val simpleSink = Sink.foreach(println)
  val graph = simpleSource.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right)
  /*
  graph.run().onComplete{
    case Success(_) => println(s"Stream processing finished")
    case Failure(exception) => println(s"Stream processing failed: $exception")
  }
   */
  // sugars
  val sum: Future[Int] = Source(1 to 10).runWith(Sink.reduce[Int](_+_))//source.to(Sink.reduce)(Keep.right)
  /*
  sum.onComplete{
    case Success(value) => println(s"the sum of the all element: $value")
    case Failure(exception) => println(s"exception: $exception")
  }

   */
  Source(1 to 11).runReduce(_+_).onComplete{
    case Success(value) => println(s"the sum of the all element: $value")
    case Failure(exception) => println(s"exception: $exception")
  }
  val f1 = Source(1 to 10).toMat(Sink.last)(Keep.right).run()
  val f2 = Source(1 to 10).runWith(Sink.last) // same with abow command

  val sentenceSource = Source(List("Akka is awesome","I love streams","Materialized values are killin me"))

  val wordCountSink = Sink.fold[Int,String](0)((currentWords, newSentence) => currentWords + newSentence.split(" ").length)
  val g1 = sentenceSource.runWith(wordCountSink)
  val g2: Future[Int] = sentenceSource.toMat(wordCountSink)(Keep.right).run()//same with above command
}
