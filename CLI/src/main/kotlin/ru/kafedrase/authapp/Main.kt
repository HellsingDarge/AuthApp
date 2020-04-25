package ru.kafedrase.authapp

import kotlin.system.exitProcess
import org.flywaydb.core.Flyway

fun main(args: Array<String>) {

    val flyway = Flyway.configure()
        .dataSource(System.getenv("AuthAppDB_URL"), System.getenv("AuthAppDB_USER"), System.getenv("AuthAppDB_PASS"))
        .load()

    flyway.migrate()

    val app = Application(args)
    val returnCode = app.run()
    exitProcess(returnCode.value)
}
