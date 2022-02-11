package akka.http.highlevelserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

object HighLevelIntro extends App{
  implicit val system = ActorSystem("HighLevelIntro")
  implicit val materializer= ActorMaterializer
  import system.dispatcher

  //directives
  //DIRECTIVES => what happens under which conditions
  import akka.http.scaladsl.server.Directives._
  val simpleRoute: Route =
    path("home"){ //DIRECTIVE -> filter input url root path
      complete(StatusCodes.OK) //DIRECTIVE -> define reponse status
    }

  val pathGetRoute: Route =
    path("home"){ //DIRECTIVE -> filter input url root path
      get {
        complete(StatusCodes.OK) //DIRECTIVE -> define reponse status
      }
    }

  //changeing directives with ~ operator
  val chainedRoute = path("myEndpoint"){
    get{
      complete(StatusCodes.OK)
    } ~
    post{
      complete(StatusCodes.Forbidden)
    }
  }~
  path("home"){
    complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        """
          |<html>
          |<body>
          |Hello from the high level AKKA http
          |</body>
          |<html/>
        """.stripMargin
      )
    )
  }

  Http().bindAndHandle(chainedRoute,"localhost", 8080)
}
