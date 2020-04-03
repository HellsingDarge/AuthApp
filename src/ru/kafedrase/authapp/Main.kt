package ru.kafedrase.authapp

import org.flywaydb.core.Flyway
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val flyway = Flyway.configure()
        .dataSource("jdbc:h2:./AuthApp", "sa", "")
        .locations("resources/db/migration")
        .load()

    flyway.migrate()

    val app = Application(args)
    val returnCode = app.run()
    exitProcess(returnCode.value)
}