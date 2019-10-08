import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient

trait Route {
  val route: String
}

trait Action

object HEAT extends Route with Action {
  override val route: String = "/action/heat"
}
object COOL extends Route with Action {
  override val route: String = "/action/cool"
}
object STOP extends Route with Action {
  override val route: String = "/action/stop"
}
object TEMPERATURE extends Route {
  override val route: String = "/temperature"
}
object NO_ACTION extends Action

case class SimpleThermostat(private val digitalTwinHost: String,
                            private val digitalTwinPort: Int,
                            private val temperature: Int) extends Thread {

  //private var currentTemperature: Double = 0.0
  private val threshold: Double = 0.5
  private val minTemp = this.temperature - this.threshold
  private val maxTemp = this.temperature + this.threshold
  private val vertx: Vertx = Vertx.vertx()
  private val client: WebClient = WebClient.create(vertx)
  //private var nextAction: Action = NO_ACTION

  override def run(): Unit = () => {
    while(true) {
      client.get(digitalTwinPort, digitalTwinHost, TEMPERATURE.route)
        .send(ar =>
          if (ar.succeeded()) {
            val result = ar.result().bodyAsJsonObject()
            if (result.containsKey("temperature")) {
              val currentTemperature = result.getDouble("temperature")
              println(currentTemperature)
              currentTemperature match {
                case temp if temp >= this.minTemp && temp <= this.maxTemp => client.post(digitalTwinPort, digitalTwinHost, STOP.route).send(_ => println("start heat"))
                case temp if temp < this.minTemp => client.post(digitalTwinPort, digitalTwinHost, HEAT.route).send(_ => println("start cool"))
                case temp if temp > this.maxTemp => client.post(digitalTwinPort, digitalTwinHost, COOL.route).send(_ => println("stop"))
                case _ =>
              }
            }
          }
        )
    }
    /*sens()
    think()
    act()*/
  }

  /*private def sens(): Unit = () => {
    client.get(digitalTwinPort, digitalTwinHost, TEMPERATURE.route)
      .send(ar =>
        if (ar.succeeded() && ar.result().statusCode() == 200)
          currentTemperature = ar.result().bodyAsJsonObject().getDouble("temperature", currentTemperature)
      )
  }

  private def think(): Unit = currentTemperature match {
    case temp if temp >= this.minTemp && temp <= this.maxTemp => nextAction = STOP
    case temp if temp < this.minTemp => nextAction = HEAT
    case temp if temp > this.maxTemp => nextAction = COOL
    case _ => nextAction = NO_ACTION
  }

  private def act(): Unit = nextAction match {
    case STOP => client.post(digitalTwinPort, digitalTwinHost, STOP.route)
    case HEAT => client.post(digitalTwinPort, digitalTwinHost, HEAT.route)
    case COOL => client.post(digitalTwinPort, digitalTwinHost, COOL.route)
    case NO_ACTION =>
  }*/
}

object Thermostat extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  SimpleThermostat(digitalTwinHost, digitalTwinPort, temperature).start()
}
