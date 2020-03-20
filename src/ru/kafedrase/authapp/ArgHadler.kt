package ru.kafedrase.authapp

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.services.AuthenticationService
import java.text.SimpleDateFormat
import java.util.*

class ArgHandler(args: Array<String>) {

    class InvalidActivity: Throwable()
    class InvalidLoginFormat: Throwable()
    class UnknownRole: Throwable()
    class InvalidResource: Throwable()
    class InvalidPassword: Throwable()

    private val parser = ArgParser("app.jar", true)

    private val login: String? by parser.option(
        ArgType.String,
        shortName = "login",
        description = "Логин пользователя, строка, строчные буквы. Не более 10 символов"
    )
    private val password: String? by parser.option(
        ArgType.String,
        shortName = "pass",
        description = "Пароль"
    )
    private val resource: String? by parser.option(
        ArgType.String,
        shortName = "res",
        description = "Абсолютный путь до запрашиваемого ресурса, формат A.B.C"
    )
    private val role: String? by parser.option(
        ArgType.String,
        shortName = "role",
        description = "Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE"
    )
    private val dateStart: String? by parser.option(
        ArgType.String,
        shortName = "ds",
        description = "Дата начала сессии с ресурсом, формат YYYY-MM-DD "
    )
    private val dateEnd: String? by parser.option(
        ArgType.String,
        shortName = "de",
        description = "Дата окончания сессии с ресурсом, формат YYYY-MM-DD "
    )
    private val volume: String? by parser.option(
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

    private fun isLoginValid(login: String?) = !login.isNullOrBlank() && login.matches(Regex("[a-z]{1,10}"))

    fun isRoleValid(role: String?) = !role.isNullOrBlank() && Role.getNames().contains(role)

    private fun parseDate(date: String?): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.isLenient = false
        return formatter.parse(date)
    }

    fun getValidLogin(): String {
        val login = this.login
        return if (login != null && isLoginValid(login)) login
        else throw InvalidLoginFormat()
    }

    fun getValidPassword(): String {
        val password = this.password
        return if (password != null) return password
        else throw InvalidPassword()
    }

    fun getValidRole(): Role {
        val role = this.role
        if (role != null && isRoleValid(role)) return Role.valueOf(role)
        else throw UnknownRole()
    }

    fun getValidResource(): String {
        val resource = this.resource
        if (resource != null) return resource
        else throw InvalidResource()
    }

    fun getUserSession(): UserSession {
        val dateStart = parseDate(dateStart)
        val dateEnd = parseDate(dateEnd)
        val volume = this.volume!!.toInt()
        val login = this.login
        val resource = this.resource

        return if (login != null && resource != null && !(dateStart.after(dateEnd) || volume < 1)) {
            UserSession(login, resource, dateStart, dateEnd, volume)
        } else {
            throw InvalidActivity()
        }
    }

    fun printHelp() {
        println(
            """
            Usage: app.jar options_list
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
