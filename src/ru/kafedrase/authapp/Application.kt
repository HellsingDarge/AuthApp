package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.*
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

        if (!argHandler.isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        val authenResult = authenService.start(argHandler.login!!, argHandler.password!!)
        if (authenResult.second == AuthenticationResultType.UNKNOWN_LOGIN)
            return UNKNOWN_LOGIN

        if (authenResult.second == AuthenticationResultType.INVALID_PASSWORD)
            return INVALID_PASSWORD

        if (!argHandler.canAuthorise() && authenResult.second == AuthenticationResultType.SUCCESS)
            return SUCCESS

        if (!argHandler.isRoleValid(argHandler.role))
            return UNKNOWN_ROLE

        resourceRepository = ResourceRepository()
        authorService = AuthorizationService(
            UsersResources(
                argHandler.resource,
                Role.valueOf(argHandler.role!!),
                authenService.currentUser.login
            ),
            resourceRepository
        )

        if (!authorService.haveAccess())
            return NO_ACCESS

        if (!argHandler.canAccount())
            return SUCCESS

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