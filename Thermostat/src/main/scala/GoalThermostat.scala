import model.DigitalTwinModel

class GoalThermostat(private val model: DigitalTwinModel, private val goalTemperature: Int) extends Thread {

  private val threshold: Double = 0.5
  private val minTemp = this.goalTemperature - this.threshold
  private val maxTemp = this.goalTemperature + this.threshold
  private var hasToWork = true

  private var state: State = STOPPED
  private var roomTemperature: Option[Double] = None

  override def run() {
    while(hasToWork) {
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
    println("GOAL ACHIEVED: the temperature in the room is now " + goalTemperature)
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

object ThermostatApp extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  val digitalTwinModel = DigitalTwinModel(digitalTwinHost, digitalTwinPort)
  new GoalThermostat(digitalTwinModel, temperature).start()
}
