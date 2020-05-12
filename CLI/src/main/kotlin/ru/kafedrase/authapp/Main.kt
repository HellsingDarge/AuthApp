package ru.kafedrase.authapp

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    DBWrapper.initDB()

    val app = Application(args)
    val returnCode = app.run()

    DBWrapper.closePool()

    exitProcess(returnCode.value)
}
