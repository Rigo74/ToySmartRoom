package model

import java.util.concurrent.CompletableFuture

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.{HttpRequest, WebClient}

trait Action {
  val route: String
}

object HEAT extends Action {
  override val route: String = "/action/heat"
}
object COOL extends Action {
  override val route: String = "/action/cool"
}
object STOP extends Action {
  override val route: String = "/action/stop"
}
object TEMPERATURE extends Action {
  override val route: String = "/temperature"
}
object STATE extends Action {
  override val route: String = "/state"
}

case class DigitalTwinModel(private val host: String, private val port: Int) {

  private val vertx: Vertx = Vertx.vertx()
  private val client: WebClient = WebClient.create(vertx)

  def getTemperature: Option[Double] = getBlocking(TEMPERATURE) match {
    case result if result.containsKey("temperature") => Some(result.getDouble("temperature"))
    case _ => None
  }

  def getState: Option[String] = getBlocking(STATE) match {
    case result if result.containsKey("state") => Some(result.getString("state"))
    case _ => None
  }

  def startHeating(): Unit = postBlocking(HEAT)

  def startCooling(): Unit = postBlocking(COOL)

  def stop(): Unit = postBlocking(STOP)

  private def getBlocking(action: Action): JsonObject = blockingCallWithResult(client.get(port, host, action.route))

  private def postBlocking(action: Action): Unit = blockingCallWithoutResult(client.post(port, host, action.route))

  private def blockingCallWithResult(request: HttpRequest[Buffer]): JsonObject = {
    val future = new CompletableFuture[JsonObject]()
    request send(ar =>
      future complete (
        if (ar.succeeded()) ar.result.bodyAsJsonObject
        else new JsonObject()
      )
    )
    future get()
  }

  private def blockingCallWithoutResult(request: HttpRequest[Buffer]): Unit = {
    val future = new CompletableFuture[Any]()
    request.send(_ => future complete())
    future get()
  }
}
