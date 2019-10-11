import model.DigitalTwinModel

trait State {
  val text: String
}
object STOPPED extends State {
  val text = "STOPPED"
}
object COOLING extends State {
  val text = "COOLING"
}
object HEATING extends State {
  val text = "HEATING"
}
object UNKNOWN extends State {
  val text = "UNKNOWN"
}

class SimpleThermostat(private val model: DigitalTwinModel, private val temperature: Int) extends Thread {

  private val threshold: Double = 0.5
  private val minTemp = this.temperature - this.threshold
  private val maxTemp = this.temperature + this.threshold
  private var hasToWork = true

  override def run() {
    while(hasToWork) {
      val state = model getState match {
        case Some(s) if s == STOPPED.text => STOPPED
        case Some(s) if s == HEATING.text => HEATING
        case Some(s) if s == COOLING.text => COOLING
        case _ => UNKNOWN
      }
      model getTemperature match {
        case Some(temp) if temp < this.minTemp && state != HEATING => model.startHeating()
        case Some(temp) if temp > this.maxTemp && state != COOLING => model.startCooling()
        case Some(temp) if temp >= this.minTemp && temp <= this.maxTemp && state != STOPPED =>
          model.stop()
          hasToWork = false
        case _ =>
      }
    }
    println("GOAL ACHIEVED: the temperature in the room is now " + temperature)
  }
}

object Thermostat extends App {
  val digitalTwinHost = "localhost"
  val digitalTwinPort = 3000
  val temperature = 23
  val digitalTwinModel = DigitalTwinModel(digitalTwinHost, digitalTwinPort)
  new SimpleThermostat(digitalTwinModel, temperature).start()
}
