package ru.kafedrase.authapp

import kotlin.system.exitProcess
import org.flywaydb.core.Flyway

fun main(args: Array<String>) {

    val flyway = Flyway.configure()
        .dataSource("jdbc:h2:./AuthApp", "sa", "")
        .locations("resources/db/migrations")
        .load()

    flyway.migrate()

    val app = Application(args)
    val returnCode = app.run()
    exitProcess(returnCode.value)
}
