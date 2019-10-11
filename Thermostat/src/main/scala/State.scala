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