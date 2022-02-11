package akka.http.low_level_server

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source,Flow}
import javax.print.DocFlavor.URL

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object LowLevelAPI extends App{
  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val serverSource: Source[IncomingConnection, Future[Http.ServerBinding]] = Http().bind("localhost",8000)
  val connectionSink: Sink[IncomingConnection, Future[Done]] = Sink.foreach[IncomingConnection]{ connection =>
  println(s"Accepted incomin connection from ${connection.remoteAddress}")
  }
  val serverBindingFuture: Future[Http.ServerBinding] = serverSource.to(connectionSink).run()
  serverBindingFuture.onComplete{
    case Success(binding) =>
      println("Server binding successful")
      binding.terminate(2 seconds)
    case Failure(ex) =>println(s"Server binding failed: $ex")
  }
  // synchronously handle requests
  //single thread

  val requestHandler:HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, _, _, _, _) =>
      HttpResponse(
        StatusCodes.OK, entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |Hello from akka http
            |</body>
            |</html>
          """.stripMargin
        )
      )
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              |<body>
              |OOPS resource cant be found
              |</body>
              |</html>
            """.stripMargin
        )

      )
  }
  val httpSyncConnectionHandler = Sink.foreach[IncomingConnection]{connection =>
    connection.handleWithSyncHandler(requestHandler)
  }
  //Http().bind("localhost", 8080).runWith(httpSyncConnectionHandler)
  //shorhand version
  //Http().bindAndHandleSync(requestHandler,"localhost",8000)


  //METHOD 2
  //Serve back http response asynchronously

  val asyncRequestHandler:HttpRequest => Future[HttpResponse]= {
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) =>
      Future(HttpResponse(
        StatusCodes.OK, entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |Hello from akka http
            |</body>
            |</html>
          """.stripMargin
        )
      )
    )
    case request: HttpRequest =>
      request.discardEntityBytes()
      Future(HttpResponse(
        StatusCodes.NotFound,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |OOPS resource cant be found
            |</body>
            |</html>
          """.stripMargin
        )

      )
    )
  }
  val httpAsyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
    connection.handleWithAsyncHandler(asyncRequestHandler)
    //Http().bindAndHandleAsync(httpAsyncConnectionHandler, "localhost", 8081)
  }

  //method 3
  //stream
  val streamRequestHandler: Flow[HttpRequest,HttpResponse,_] = Flow[HttpRequest].map{
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) =>
      HttpResponse(
        StatusCodes.OK, entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |Hello from akka http
            |</body>
            |</html>
          """.stripMargin
        )
      )
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |OOPS resource cant be found
            |</body>
            |</html>
          """.stripMargin
        )
      )
  }
  //stream based
  //manual version
  Http().bind("localhost", 8082).runForeach(connection =>
    connection.handleWith(streamRequestHandler)
  )
  //shorthand version
  Http().bindAndHandle(streamRequestHandler,"localhost",8082 )

}
