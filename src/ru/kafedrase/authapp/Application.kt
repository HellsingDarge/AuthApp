package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.domain.UserSession
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.*


class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)
    private val userRepository = UserRepository()
    private val authenticationService = AuthenticationService(userRepository)
    private val userService = UserService()
    private lateinit var resourceRepository: ResourceRepository
    private lateinit var authorizationService: AuthorizationService
    private val accountingService = AccountingService()

    fun run(): ExitCode {
        if (argHandler.shouldPrintHelp()) {
            argHandler.printHelp()
            return HELP
        }

        if (!userService.isLoginValid(argHandler.login))
            return INVALID_LOGIN_FORMAT

        if (!authenticationService.start(argHandler.login!!))
            return UNKNOWN_LOGIN

        if (!authenticationService.verifyPass(argHandler.password!!))
            return WRONG_PASSWORD

        if (!argHandler.canAuthorise())
            return SUCCESS

        if (!userService.isRoleValid(argHandler.role))
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

        val dateStart = userService.parseDate(argHandler.dateStart!!)
        val dateEnd = userService.parseDate(argHandler.dateEnd!!)
        val volume = userService.parseVolume(argHandler.volume!!)

        if (!userService.areDatesValid(dateStart, dateEnd) || !userService.isVolumeValid(volume))
            return INVALID_ACTIVITY

        accountingService.write(
            UserSession(
                authenticationService.currentUser, authorizationService.usersResource.path,
                dateStart!!, dateEnd!!, volume!!
            )
        )

        return SUCCESS
    }
}