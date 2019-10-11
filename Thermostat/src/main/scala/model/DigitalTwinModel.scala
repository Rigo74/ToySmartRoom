package model

import java.util.concurrent.Semaphore

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

  private def getBlocking(action: Action): JsonObject = blockingCallWithResult(client.get(port, host, action route))

  private def postBlocking(action: Action): Unit = blockingCallWithoutResult(client.post(port, host, action route))

  private def blockingCallWithResult(request: HttpRequest[Buffer]): JsonObject = {
    val semaphore = new Semaphore(0)
    var result = Option.empty
    request.send(ar => {
      if (ar.succeeded())
        result = Some(ar.result().bodyAsJsonObject())
      semaphore.release()
    })
    semaphore.acquire()
    result get
  }

  private def blockingCallWithoutResult(request: HttpRequest[Buffer]): Unit = {
    val semaphore = new Semaphore(0)
    request.send(_ => semaphore.release())
    semaphore.acquire()
  }
}
