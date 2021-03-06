package ru.kafedrase.authapp

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

object ArgHandler {
    lateinit var arguments: Array<String>
    private val parser = ArgParser("AuthApp.jar", true)

    val login: String? by parser.option(
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

    fun parse() {
        try {
            parser.parse(arguments)
        } catch (ex: IllegalStateException) {
        }
    }

    fun canAuthenticate() = !(login.isNullOrBlank() || password.isNullOrBlank())

    fun canAuthorise() = !(resource.isNullOrEmpty() || role.isNullOrEmpty())

    fun canAccount() = !(dateStart.isNullOrEmpty() || dateEnd.isNullOrEmpty() || volume == null)

    fun shouldPrintHelp() = !canAuthenticate()

    fun isLoginValid(login: String?) = !login.isNullOrBlank() && login.matches(Regex("[a-z]{1,10}"))

    fun isRoleValid(role: String?) = !role.isNullOrBlank() && Role.getNames().contains(role)

    fun parseDate(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(date, formatter)
    }

    fun printHelp() {
        println(
            """
            Usage: AuthApp.jar options_list
            Options: 
                -login -> Логин пользователя, строка, строчные буквы. Не более 10 символов { String }
                -pass -> Пароль { String }
                -res -> Абсолютный путь до запрашиваемого ресурса, формат A.B.C { String }
                -role -> Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE { String }
                -ds -> Дата начала сессии с ресурсом, формат YYYY-MM-DD  { String }
                -de -> Дата окончания сессии с ресурсом, формат YYYY-MM-DD  { String }
                -vol -> Потребляемый объем, целое число { String }
                -h -> Usage info 
            """.trimIndent()
        )
    }
}
