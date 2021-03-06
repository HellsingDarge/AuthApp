package ru.kafedrase.authapp

import com.google.inject.Inject
import java.time.format.DateTimeParseException
import javax.sql.DataSource
import org.apache.logging.log4j.LogManager
import ru.kafedrase.authapp.dao.AccountingDAO
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.AccountingService
import ru.kafedrase.authapp.services.AuthenticationService
import ru.kafedrase.authapp.services.AuthorisationService

class Application @Inject constructor(private val dataSource: DataSource) {
    private val argHandler = ArgHandler

    fun run(): ExitCode {
        val logger = LogManager.getLogger()

        logger.debug("Passed arguments: " + argHandler.arguments.joinToString(" "))

        if (argHandler.shouldPrintHelp()) {
            argHandler.printHelp()
            return ExitCode.HELP
        }

        if (!argHandler.isLoginValid(argHandler.login)) {
            logger.error("Received invalid login: ${argHandler.login}")
            return ExitCode.INVALID_LOGIN_FORMAT
        }

        // todo instead of giving all services admin rights, give each service only the needed one
        dataSource.connection.use { connection ->
            val usersCredentialsDAO = AuthenticationDAO(connection)
            val authenticationService = AuthenticationService(usersCredentialsDAO)

            if (!authenticationService.start(argHandler.login!!)) {
                logger.error("Couldn't find user for login: ${argHandler.login}")
                return ExitCode.UNKNOWN_LOGIN
            }

            if (!authenticationService.verifyPass(argHandler.password!!)) {
                logger.error("Password didn't match for user: ${argHandler.login}")
                return ExitCode.WRONG_PASSWORD
            }

            if (!argHandler.canAuthorise()) {
                logger.info("Successfully authenticated user: ${argHandler.login}")
                return ExitCode.SUCCESS
            }

            if (!argHandler.isRoleValid(argHandler.role)) {
                logger.error("Received invalid role: ${argHandler.role}")
                return ExitCode.UNKNOWN_ROLE
            }

            val authorisationDAO = AuthorisationDAO(connection)

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
                return ExitCode.NO_ACCESS
            }

            if (!argHandler.canAccount()) {
                logger.info("Successfully authorised user")
                return ExitCode.SUCCESS
            }

            val accountingDAO = AccountingDAO(connection)
            val accountingService = AccountingService(accountingDAO)

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
                    return ExitCode.INVALID_ACTIVITY
                }
                accountingService.write(
                        UserSession(
                                authenticationService.currentUser,
                                authorizationService.resource!!.id,
                                dateStart,
                                dateEnd,
                                volume
                        )
                )
            } catch (e: NumberFormatException) {
                logger.error("Couldn't parse argument vol", e)
                return ExitCode.INVALID_ACTIVITY
            } catch (e: DateTimeParseException) {
                logger.error("Couldn't parse either ds or de", e)
                return ExitCode.INVALID_ACTIVITY
            }
        }

        logger.debug("Completed AAA successfully")
        return ExitCode.SUCCESS
    }
}
