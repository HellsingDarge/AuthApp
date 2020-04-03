package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.AccountingService
import ru.kafedrase.authapp.services.AuthenticationService
import ru.kafedrase.authapp.services.AuthorisationService
import java.sql.DriverManager
import java.time.format.DateTimeParseException

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)

    fun run(): ExitCode {
        if (argHandler.shouldPrintHelp()) {
            argHandler.printHelp()
            return HELP
        }

        if (!argHandler.isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        // todo instead of giving all services admin rights, give each service only the needed one
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

        val authorisationDAO = AuthorisationDAO(dbConnection)

        val authorizationService = AuthorisationService(
            UsersResources(
                argHandler.resource!!,
                Role.valueOf(argHandler.role!!),
                authenticationService.currentUser.login
            ),
            authorisationDAO
        )

        if (!authorizationService.haveAccess())
            return NO_ACCESS

        if (!argHandler.canAccount())
            return SUCCESS

        val accountingService = AccountingService(dbConnection)

        try {
            val dateStart = argHandler.parseDate(argHandler.dateStart!!)
            val dateEnd = argHandler.parseDate(argHandler.dateEnd!!)
            val volume = argHandler.volume!!.toInt()

            if (dateStart.isAfter(dateEnd) || volume < 1)
                return INVALID_ACTIVITY

            accountingService.write(
                UserSession(
                    authenticationService.currentUser,
                    Role.valueOf(argHandler.role!!), // fixme
                    authorizationService.usersResource.path,
                    dateStart,
                    dateEnd,
                    volume
                )
            )

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is DateTimeParseException -> return INVALID_ACTIVITY
                else -> throw e
            }
        }

        dbConnection.close()

        return SUCCESS
    }
}