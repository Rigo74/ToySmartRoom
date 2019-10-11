import model.DigitalTwinModel

class MaintenanceThermostat(private val model: DigitalTwinModel, private val idealTemperature: Int) extends Thread {

  private val threshold: Double = 0.5
  private val minTemp = this.idealTemperature - this.threshold
  private val maxTemp = this.idealTemperature + this.threshold
  private var hasToWork = true

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
            hasToWork = false
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
  }
}

object ThermostatApp2 extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  val digitalTwinModel = DigitalTwinModel(digitalTwinHost, digitalTwinPort)
  new MaintenanceThermostat(digitalTwinModel, temperature).start()
}
