package proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

trait Service {

  val forwardTo: Uri
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  implicit val executionContext = system.dispatcher

  val routes: HttpRequest => Future[HttpResponse] = {

    case HttpRequest(method, Uri.Path("/test"), headers, entity, _) =>
      Future.successful(HttpResponse(entity="Hello test route!"))

    case HttpRequest(method, Uri.Path(path), headers, entity, _) =>

      val request: HttpRequest = HttpRequest(method, forwardTo.withPath(Uri.Path(path)))
        .withHeaders(headers)
        .withEntity(entity)
      Http().singleRequest(request)

  }
}

class Proxy(val forwardTo: Uri,
             implicit val system:ActorSystem,
            implicit  val materializer:ActorMaterializer) extends Service {

  def listen(scheme: String, host: String, port: Int) = {
    Http().bindAndHandleAsync(routes, host, port)
    println(s"Server online at ${scheme}://${host}:" + port + "/\nPress RETURN to stop...")
    println(s"Forwarding requests to ${forwardTo}")

    scala.sys.addShutdownHook(new Thread() {
      override def run() {
        println("Terminating...")
        system.terminate()
        Await.result(system.whenTerminated, 30 seconds)
        println("Terminated... Bye")

      }
    })
  }
}

object Server extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val serverAddr = sys.env("TARGET_SERVER")
  val proxyAddr = sys.env("PROXY_SERVER")
  val proxyUri = Uri(proxyAddr)

  val serverUri = Uri(serverAddr)
  val proxy = new Proxy(serverUri, system, materializer)
  proxy.listen(proxyUri.scheme, proxyUri.authority.host.toString(), proxyUri.effectivePort)
}
