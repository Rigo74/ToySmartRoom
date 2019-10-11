import model.DigitalTwinModel

class UserThermostat(private val model: DigitalTwinModel, private val startTemperature: Int) extends Thread {

  private val userView = new UserView

  private val threshold: Double = 0.5
  private var idealTemperature = this.startTemperature
  private var minTemp = this.startTemperature - this.threshold
  private var maxTemp = this.startTemperature + this.threshold

  private var state: State = STOPPED
  private var roomTemperature: Option[Double] = None

  override def run() {
    while(true) {
      sense()
      while (roomTemperature exists (temp => temp > maxTemp || temp < minTemp)) {
        sense()
        this.roomTemperature match {
          case Some(temp) if temp < this.minTemp && state != HEATING => model.startHeating()
          case Some(temp) if temp > this.maxTemp && state != COOLING => model.startCooling()
          case Some(temp) if temp >= this.minTemp && temp <= this.maxTemp && state != STOPPED =>
            model.stop()
          case _ =>
        }
      }
    }
  }

  private def sense(): Unit = {
    this.state = model.getState match {
      case Some(s) if s == STOPPED.text => STOPPED
      case Some(s) if s == HEATING.text => HEATING
      case Some(s) if s == COOLING.text => COOLING
      case _ => UNKNOWN
    }
    this.roomTemperature = model.getTemperature
    val temperature = userView.getCurrentUserTemperature
    temperature
      .filter(temp => temp != idealTemperature)
      .ifPresent(temp => {
        idealTemperature = temp
        minTemp = temp - this.threshold
        maxTemp = temp + this.threshold
      })
  }
}

object ThermostatApp3 extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  val digitalTwinModel = DigitalTwinModel(digitalTwinHost, digitalTwinPort)
  new UserThermostat(digitalTwinModel, temperature).start()
}
