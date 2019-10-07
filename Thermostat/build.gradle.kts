plugins {
    java
    scala
    application
}

application {
    mainClassName="Thermostat"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.12.2")

    implementation("io.vertx:vertx-web-client:3.8.2")
}
