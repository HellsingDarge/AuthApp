package ru.kafedrase.authapp


import org.apache.logging.log4j.LogManager
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


class Application(private val args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)

    fun run(): ExitCode {
        val logger = LogManager.getLogger()

        logger.debug("Passed arguments: " + args.joinToString(" "))

        if (argHandler.shouldPrintHelp()) {
            logger.error("Not enough parameters")
            argHandler.printHelp()
            return HELP
        }

        if (!argHandler.isLoginValid(argHandler.login)) {
            logger.error("Received invalid login: ${argHandler.login}")
            return INVALID_LOGIN_FORMAT
        }
        // todo instead of giving all services admin rights, give each service only the needed one
        val dbUrl = System.getenv("H2URL") ?: "jdbc:h2:./AuthApp"
        val dbUser = System.getenv("H2USER") ?: "sa"
        val dbPass = System.getenv("H2PASSWORD") ?: ""
        val dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass)

        dbConnection.use {
            val usersCredentialsDAO = AuthenticationDAO(dbConnection)
            val authenticationService = AuthenticationService(usersCredentialsDAO)

            if (!authenticationService.start(argHandler.login!!)) {
                logger.error("Couldn't find user for login: ${argHandler.login}")
                return UNKNOWN_LOGIN
            }

            if (!authenticationService.verifyPass(argHandler.password!!)) {
                logger.error("Password didn't match for user: ${argHandler.login}")
                return WRONG_PASSWORD
            }
            if (!argHandler.canAuthorise()) {
                logger.info("Successfully authenticated user: ${argHandler.login}")
                return SUCCESS
            }

            if (!argHandler.isRoleValid(argHandler.role)) {
                logger.error("Received invalid role: ${argHandler.role}")
                return UNKNOWN_ROLE
            }
            val authorisationDAO = AuthorisationDAO(dbConnection)

            val authorizationService = AuthorisationService(
                    UsersResources(
                            0,
                            argHandler.resource!!,
                            Role.valueOf(argHandler.role!!),
                            authenticationService.currentUser.login
                    ),
                    authorisationDAO
            )

            if (!authorizationService.haveAccess()) {
                logger.error("User \"${argHandler.login}\" with role \"${argHandler.role}\" doesn't have access to resource \"${argHandler.resource}\"")
                return NO_ACCESS
            }
            if (!argHandler.canAccount()) {
                logger.info("Successfully authorised user")
                return SUCCESS
            }
            val accountingService = AccountingService(dbConnection)

            try {
                val dateStart = argHandler.parseDate(argHandler.dateStart!!)
                val dateEnd = argHandler.parseDate(argHandler.dateEnd!!)
                val volume = argHandler.volume!!.toInt()

                if (dateStart.isAfter(dateEnd) || volume < 1) {
                    logger.error(
                            """Received invalid date or volume.
                        |ds: $dateStart
                        |ds: $dateEnd
                        |vol: $volume
                    """.trimMargin()
                    )
                    return INVALID_ACTIVITY
                }
                accountingService.write(
                        UserSession(
                                authenticationService.currentUser,
                                authorizationService.usersResource.id,
                                dateStart,
                                dateEnd,
                                volume
                        )
                )

            } catch (e: Exception) { //fixme вот тут разве нелья сделать несколько catch?
                when (e) {
                    is NumberFormatException, is DateTimeParseException -> {
                        logger.error("Couldn't parse date or volume.", e)
                        return INVALID_ACTIVITY
                    }
                    else -> {
                        logger.fatal("Uncaught exception.", e)
                        throw e
                    }
                }
            }
        }
        logger.debug("Completed AAA successfully")
        return SUCCESS
    }
}