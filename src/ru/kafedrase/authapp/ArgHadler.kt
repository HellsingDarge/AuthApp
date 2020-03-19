package ru.kafedrase.authapp

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class ArgHandler(args: Array<String>) {
    private val parser = ArgParser("app.jar", true)

    var login: String? by parser.option(
        ArgType.String,
        shortName = "login",
        description = "Логин пользователя, строка, строчные буквы. Не более 10 символов"
    )
    val password: String? by parser.option(
        ArgType.String,
        shortName = "pass",
        description = "Пароль"
    )
    val resource: String? by parser.option(
        ArgType.String,
        shortName = "res",
        description = "Абсолютный путь до запрашиваемого ресурса, формат A.B.C"
    )
    val role: String? by parser.option(
        ArgType.String,
        shortName = "role",
        description = "Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE"
    )
    val dateStart: String? by parser.option(
        ArgType.String,
        shortName = "ds",
        description = "Дата начала сессии с ресурсом, формат YYYY-MM-DD "
    )
    val dateEnd: String? by parser.option(
        ArgType.String,
        shortName = "de",
        description = "Дата окончания сессии с ресурсом, формат YYYY-MM-DD "
    )
    val volume: String? by parser.option(
        ArgType.String,
        shortName = "vol",
        description = "Потребляемый объем, целое число"
    )

    init {
        try {
            parser.parse(args)
        } catch (ex: IllegalStateException) {
        }
    }

    fun canAuthenticate() = !(login.isNullOrBlank() || password.isNullOrBlank())

    fun canAuthorise() = !(resource.isNullOrEmpty() || role.isNullOrEmpty())

    fun canAccount() = !(dateStart.isNullOrEmpty() || dateEnd.isNullOrEmpty() || volume == null)

    fun shouldPrintHelp() = !canAuthenticate()
}
