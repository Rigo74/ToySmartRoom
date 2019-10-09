import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient

trait Route {
  val route: String
}

object HEAT extends Route {
  override val route: String = "/action/heat"
}
object COOL extends Route {
  override val route: String = "/action/cool"
}
object STOP extends Route {
  override val route: String = "/action/stop"
}
object TEMPERATURE extends Route {
  override val route: String = "/temperature"
}

class SimpleThermostat(private val digitalTwinHost: String,
                            private val digitalTwinPort: Int,
                            private val temperature: Int) extends Thread {

  private val threshold: Double = 0.5
  private val minTemp = this.temperature - this.threshold
  private val maxTemp = this.temperature + this.threshold
  private val vertx: Vertx = Vertx.vertx()
  private val client: WebClient = WebClient.create(vertx)

  override def run() {
    work()
  }

  private def work(): Unit = {
    client.get(digitalTwinPort, digitalTwinHost, TEMPERATURE.route)
      .send(ar => {
        if (ar.succeeded()) {
          val result = ar.result().bodyAsJsonObject()
          if (result.containsKey("temperature")) {
            val currentTemperature = result.getDouble("temperature", 0.0)
            println(currentTemperature)
            (currentTemperature match {
              case temp if temp >= this.minTemp && temp <= this.maxTemp => client.post(digitalTwinPort, digitalTwinHost, STOP.route)//.send(_ => println("start heat"))
              case temp if temp < this.minTemp => client.post(digitalTwinPort, digitalTwinHost, HEAT.route)//.send(_ => println("start cool"))
              case temp if temp > this.maxTemp => client.post(digitalTwinPort, digitalTwinHost, COOL.route)//.send(_ => println("stop"))
            }).send(ar => work())
          } else {
            work()
          }
        } else {
          work()
        }
      }
      )
  }

}

object Thermostat extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  new SimpleThermostat(digitalTwinHost, digitalTwinPort, temperature).start()
}
