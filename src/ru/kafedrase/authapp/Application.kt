package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.AccountingService
import ru.kafedrase.authapp.services.AuthenticationService
import ru.kafedrase.authapp.services.AuthorizationService
import ru.kafedrase.authapp.services.UserRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)
    private val userRepository = UserRepository()
    private val authenService = AuthenticationService(userRepository)
    private lateinit var authorService: AuthorizationService
    private val accountService = AccountingService()

    fun run(): ExitCode {
        if (!argHandler.canAuthenticate()) {
            printHelp()
            return HELP
        }

        if (!isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        val authenResult = authenService.start(argHandler.login!!, argHandler.password!!)

        if (authenResult != SUCCESS)
            return authenResult

        if (!argHandler.canAuthorise())
            return SUCCESS

        if (!isRoleValid(argHandler.role))
            return UNKNOWN_ROLE

        authorService = AuthorizationService(
            UsersResources(
                argHandler.resource,
                Role.valueOf(argHandler.role!!),
                authenService.currentUser.login
            ),
            resources
        )

        if (!authorService.haveAccess())
            return NO_ACCESS

        if (!argHandler.canAccount())
            return SUCCESS

        try {
            val dateStart = parseDate(argHandler.dateStart!!)
            val dateEnd = parseDate(argHandler.dateEnd!!)
            val volume = argHandler.volume!!.toInt()

            if (dateStart.after(dateEnd) || volume < 1)
                return INVALID_ACTIVITY

            accountService.write(
                UserSession(
                    authenService.currentUser, argHandler.resource!!,
                    dateStart, dateEnd, volume
                )
            )

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is ParseException -> return INVALID_ACTIVITY
                else -> throw e
            }
        }

        if (argHandler.shouldPrintHelp()) {
            printHelp()
            return HELP
        }

        return SUCCESS
    }

    private fun printHelp() {
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

    private fun isLoginValid(login: String?) = !login.isNullOrBlank() && login.matches(Regex("[a-z]{1,10}"))

    private fun isRoleValid(role: String?) = !role.isNullOrBlank() && Role.getNames().contains(role)

    private fun parseDate(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.isLenient = false
        return formatter.parse(date)
    }
}