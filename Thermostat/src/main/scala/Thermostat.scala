import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient

case class SimpleThermostat(temperature: Int, digitalTwinHost: String, digitalTwinPort: Int) extends Thread {

  private val getTemperature: String = "/temperature"
  private val startHeating: String = "/action/heat"
  private val startCooling: String = "/action/cool"
  private val stopWorking: String = "/action/stop"
  private var currentTemperature: Double = 0
  private var isHeating: Boolean = false
  private var isCooling: Boolean = false
  private val threshold: Double = 0.5
  private val vertx: Vertx = Vertx.vertx()
  private val client: WebClient = WebClient.create(vertx)

  override def run(): Unit = () => {
    sens()
    think
    act
  }

  private def sens(): Unit = () => {
    client.get(digitalTwinPort, digitalTwinHost, getTemperature)
      .send(ar =>
        if (ar.succeeded() && ar.result().statusCode() == 200)
          currentTemperature = ar.result().bodyAsJsonObject().getDouble("temperature", currentTemperature)
      )
  }
  private def think: Unit = ???
  private def act: Unit = ???
}

class Thermostat extends App {

}
