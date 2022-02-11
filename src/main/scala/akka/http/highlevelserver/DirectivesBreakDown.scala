package akka.http.highlevelserver

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}
import akka.stream.ActorMaterializer

object DirectivesBreakDown extends App{
  implicit val system = ActorSystem("DirectivesBreakDown")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  import akka.http.scaladsl.server.Directives._

  /*
    Type 1:filtering directives
   */

  val simpleHttpMethodRoute =
    post{//equivalent directives for get, put, patch, delete, head, options
      complete(StatusCodes.Forbidden)
    }
  val pathRoute =
    path("about"){
      complete(
        HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            |<body>
            |Hello from about page!
            |</body>
            |</html>
          """.stripMargin
        )
      )
    }
  // api/myEndpoint
  val complexPathRoute = path("api" / "myEndpoint"){
    complete(StatusCodes.OK)
  }

  //this / is encoding by route and returns api%2fendpoint
  //below route and this route are different
  val dontConfuse = path("api/myEndpoint"){
    complete(StatusCodes.OK)
  }
  //it covers both localhost:8080 and localhost:8080/
  val pathEndRoute =
    pathEndOrSingleSlash{
      complete(StatusCodes.OK)
    }
  //Http().bindAndHandle(complexPathRoute,"localhost",8080)

  /*
  Extraction directives
   */

  val pathExtractionRoute =
    path("api" / "item" / IntNumber){ItemNumber:Int =>
      println(s"I have got a number in path $ItemNumber")
      complete(StatusCodes.OK)
    }

  val pathMultipleExtractionRoute =
    path("api" / "order" / IntNumber / IntNumber){(id: Int, inverntory: Int) =>
      println(s"I have got TWO number in path $id , $inverntory")
      complete(StatusCodes.OK)
    }
  //Http().bindAndHandle(pathMultipleExtractionRoute, "localhost",8080)


  //query param extraction router
  val queryParamExtraxtionRoute =
  // /api/item?id=45
    path("api" / "item"){
      parameter("id"){ (itemId: String) =>
        println(s"I have exracted the id $itemId")
        complete(StatusCodes.OK)
      }
    }

  //query param extraction router
  val queryParamExtraxtionAndCastRoute =
  // /api/item?id=45
    path("api" / "item"){
      parameter("id".as[Int]){ (itemId: Int) =>
        println(s"I have exracted the id $itemId")
        complete(StatusCodes.OK)
      }
    }

  //query param extraction router
  val efficientQueryParamExtraxtionAndCastRoute =
  // /api/item?id=45
    path("api" / "item"){
      parameter('id.as[Int]){ (itemId: Int) => //'id cache the id query parameter as a parameter
        println(s"I have exracted the id $itemId")
        complete(StatusCodes.OK)
      }
    }

  val extractHttpRequestRoute =
    path("controlEndpoint"){
      extractRequest{ (httpRequest: HttpRequest) =>
        extractLog{ (log: LoggingAdapter) =>
          log.info(s"I got the http request: $httpRequest")
          complete(StatusCodes.OK)
        }

      }
    }
  val simpleNestedRote =
    path("api" / "item"){
      get{
        complete(StatusCodes.OK)
      }
  }
  val compactSimpleNestedRoute = (path("api" / "item") & get ){
    complete(StatusCodes.OK)
  }
  val compactExtractRequestRoute = (path("controlEndpoint") & extractRequest & extractLog) {
    (request,log) =>
      log.info(s"I got the http request: $request")
      complete(StatusCodes.OK)
  }
  val repeatedRoute = path("about"){
    complete(StatusCodes.OK)
  } ~
  path("aboutUs"){
    complete(StatusCodes.OK)
  }
  val dryRoute=(path("about") | path("aboutUs")){
    complete(StatusCodes.OK)
  }
  val blogByIdRoute = path(IntNumber){blogPostId:Int =>
    //complex server logic
    complete(StatusCodes.OK)
  }
  val blogByQueryParamRoute = parameter('postId.as[Int]){blogPostId:Int =>
    //same logic
    complete(StatusCodes.OK)
  }
  val combinedBlogByIdRoute = (path(IntNumber) | parameter('postId.as[Int])) { blogPostId: Int =>
    //original server logic
    complete(StatusCodes.OK)
  }
  /*
  Type 4 actionable directives
   */

  val completeOkRoute = complete(StatusCodes.OK)
  val failedRoute = path("notSupported"){
    failWith(new RuntimeException("Unsupported"))//Completes with http 500
  }
  val routeWithRejection =
    //path("home"){
    //  reject
    //}~
    path("index"){
      completeOkRoute
    }

  Http().bindAndHandle(compactExtractRequestRoute, "localhost",8080)
}
