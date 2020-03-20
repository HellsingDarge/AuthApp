package ru.kafedrase.authapp

import ru.kafedrase.authapp.ExitCode.*
import ru.kafedrase.authapp.services.*
import java.lang.Exception
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

        try {
            /*
                Пытаемся аутентифицировать пользователя
            */
            val login = argHandler.getValidLogin()
            val password = argHandler.getValidPassword()
            authenService.start(login, password)

            if (!argHandler.canAuthorise())
                return SUCCESS

            /*
                Пытаемся авторизовать пользователя
            */
            val role = argHandler.getValidRole()
            val resource = argHandler.getValidResource()
            resourceRepository = ResourceRepository()
            authorService = AuthorizationService(resourceRepository)

            authorService.start(
                resource,
                role,
                login
            )

            if (!argHandler.canAccount())
                return SUCCESS

            /*
                Пытаемся записать активность пользователя
            */
            accountService.write(
                argHandler.getUserSession()
            )
            return SUCCESS

        } catch (ex: ArgHandler.InvalidLoginFormat) {
            return INVALID_LOGIN_FORMAT
        } catch (ex: AuthenticationService.InvalidPassword) {
            return INVALID_PASSWORD
        } catch (ex: AuthenticationService.UnknownLogin) {
            return UNKNOWN_LOGIN
        } catch (ex: ArgHandler.UnknownRole) {
            return UNKNOWN_ROLE
        } catch (ex: AuthorizationService.NoAccess) {
            return NO_ACCESS
        } catch (ex: ArgHandler.InvalidActivity) {
            return INVALID_ACTIVITY
        } catch (e: NumberFormatException) {
            return INVALID_ACTIVITY
        } catch (e: ParseException) {
            return INVALID_ACTIVITY
        }
    }

}