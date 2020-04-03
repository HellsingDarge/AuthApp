package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.AccountingService
import ru.kafedrase.authapp.services.AuthenticationService
import ru.kafedrase.authapp.services.AuthorizationService
import ru.kafedrase.authapp.services.ResourceRepository
import java.sql.DriverManager
import java.time.format.DateTimeParseException

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)
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

        val dbConnection = DriverManager.getConnection("jdbc:h2:./AuthApp", "sa", "")

        val usersCredentialsDAO = AuthenticationDAO(dbConnection)
        val authenticationService = AuthenticationService(usersCredentialsDAO)

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
                argHandler.resource!!,
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

            if (dateStart.isAfter(dateEnd) || volume < 1)
                return INVALID_ACTIVITY

            accountingService.write(
                UserSession(
                    authenticationService.currentUser, authorizationService.usersResource.path,
                    dateStart, dateEnd, volume
                )
            )

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is DateTimeParseException -> return INVALID_ACTIVITY
                else -> throw e
            }
        }

        return SUCCESS
    }
}