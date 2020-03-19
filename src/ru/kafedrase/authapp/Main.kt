package ru.kafedrase.authapp

import ru.kafedrase.authapp.domain.User
import ru.kafedrase.authapp.domain.UsersResources
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val app = Application(args)
    val returnCode = app.run()
    exitProcess(returnCode.value)
}