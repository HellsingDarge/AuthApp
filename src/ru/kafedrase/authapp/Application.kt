package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.*
import java.text.ParseException

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)
    private val userRepository = UserRepository()
    private val authenticationService = AuthenticationService(userRepository)
    private lateinit var resourceRepository: ResourceRepository
    private lateinit var authorizationService: AuthorizationService
    private val accountingService = AccountingService()

    fun run(): ExitCode {
        if (argHandler.shouldPrintHelp()) {
            argHandler.printHelp()
            return HELP
        }

        if (!argHandler.isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        if (!authenticationService.start(argHandler.login!!))
            return UNKNOWN_LOGIN

        if (!authenticationService.verifyPass(argHandler.password!!))
            return WRONG_PASSWORD

        if (!argHandler.canAuthorise())
            return SUCCESS

        if (!argHandler.isRoleValid(argHandler.role))
            return UNKNOWN_ROLE

        resourceRepository = ResourceRepository()
        authorizationService = AuthorizationService(
            UsersResources(
                authorizationService.usersResource.path,
                Role.valueOf(argHandler.role!!),
                authenticationService.currentUser.login
            ),
            resourceRepository
        )

        if (!authorizationService.haveAccess())
            return NO_ACCESS

        if (!argHandler.canAccount())
            return SUCCESS

        try {
            val dateStart = argHandler.parseDate(argHandler.dateStart!!)
            val dateEnd = argHandler.parseDate(argHandler.dateEnd!!)
            val volume = argHandler.volume!!.toInt()

            if (dateStart.after(dateEnd) || volume < 1)
                return INVALID_ACTIVITY

            accountingService.write(
                UserSession(
                    authenticationService.currentUser, argHandler.resource!!,
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