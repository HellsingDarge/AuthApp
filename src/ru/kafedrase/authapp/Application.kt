package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.domain.User
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.*
import ru.kafedrase.authapp.services.types.AuthenticationResultType
import ru.kafedrase.authapp.services.types.AuthorizationResultType
import java.text.ParseException

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)

    private val userRepository = UserRepository()
    private val authenService = AuthenticationService(userRepository)
    private lateinit var resourceRepository: ResourceRepository
    private lateinit var authorService: AuthorizationService
    private val accountService = AccountingService()

    fun run(): ExitCode {
        if (argHandler.shouldPrintHelp()) {
            argHandler.printHelp()
            return HELP
        }

        /*
        Пытаемся аутентифицировать пользователя
         */
        if (!argHandler.isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        val authenResult = authenService.start(argHandler.login!!, argHandler.password!!)
        val authenticationResultType = authenResult.second
        if (authenticationResultType == AuthenticationResultType.UNKNOWN_LOGIN)
            return UNKNOWN_LOGIN

        if (authenticationResultType == AuthenticationResultType.INVALID_PASSWORD)
            return INVALID_PASSWORD

        if (!argHandler.canAuthorise())
            return SUCCESS
        /*
        Пытаемся авторизовать пользователя
         */
        if (!argHandler.isRoleValid(argHandler.role))
            return UNKNOWN_ROLE

        resourceRepository = ResourceRepository()
        authorService = AuthorizationService(resourceRepository)

        val authorizationResult = authorService.start(
            argHandler.resource!!,
            Role.valueOf(argHandler.role!!),
            argHandler.login!!
        )
        val authorizationResultType = authorizationResult.second
        if (authorizationResultType == AuthorizationResultType.NO_ACCESS)
            return NO_ACCESS

        if (!argHandler.canAccount())
            return SUCCESS

        /*
        Пытаемся записать активность пользователя
         */
        try {
            val dateStart = argHandler.parseDate(argHandler.dateStart!!)
            val dateEnd = argHandler.parseDate(argHandler.dateEnd!!)
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

        return SUCCESS
    }

}