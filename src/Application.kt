import ExitCode.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Application(args: Array<String>) {
    private val argHandler: ArgHandler = ArgHandler(args)
    private val authenService = AuthenticationService()
    private lateinit var authorService: AuthorizationService
    private val accountService = AccountingService()

    fun run(): ExitCode {
        if (!argHandler.canAuthenticate()) {
            return SUCCESS
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
            UsersResources(argHandler.resource, Role.valueOf(argHandler.role!!), authenService.currentUser.login)
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
            -h - напечатать эту справку
            -login <str> - логин пользователя, формат - [a-z]{10}
            -pas <st> - пароль, может быть любым, но как минимум 1 символ
            -role <str> - роль (READ, WRITE, READ)
            -res <str> - имя ресурса, полный путь заглавные буквы
            -ds <YYYY-MM-DD> - начальная дата
            -de <YYYY-MM-DD> - конечная дата
            -vol <int> - объём работы, натуральное число
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