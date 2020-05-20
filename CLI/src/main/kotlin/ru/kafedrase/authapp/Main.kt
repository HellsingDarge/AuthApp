package ru.kafedrase.authapp

import com.google.inject.Guice
import kotlin.system.exitProcess
import org.flywaydb.core.Flyway

fun main(args: Array<String>) {
    Flyway.configure()
        .dataSource(
            System.getenv("AuthAppDB_URL"),
            System.getenv("AuthAppDB_USER"),
            System.getenv("AuthAppDB_PASS")
        )
        .load()
        .migrate()

    ArgHandler.arguments = args
    ArgHandler.parse()

    val injector = Guice.createInjector(DataSourceModule())
    val app = injector.getInstance(Application::class.java)

    val returnCode = app.run()
    exitProcess(returnCode.value)
}
