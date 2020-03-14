import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val argHandler = ArgHandler(args)
    val authService = AuthService()
    var authSuccessful = false
    var authorizationSuccessful = false

    if (argHandler.canTryAuth()) {
        val login = argHandler.getArgument(ArgHandler.Arguments.LOGIN)
        val pass = argHandler.getArgument(ArgHandler.Arguments.PASSWORD)

        if (!validateLogin(login))
            exitProcess(ExitCode.INVALID_LOGIN_FORMAT.value)

        if (pass == null || !validatePass(pass))
            exitProcess(ExitCode.INVALID_PASSWORD.value)

        val currentUser = authService.getUserByLogin(login!!)

        if (!authService.userExists(currentUser))
            exitProcess(ExitCode.UNKNOWN_LOGIN.value)

        if (!authService.verifyPass(pass, currentUser!!))
            exitProcess(ExitCode.INVALID_PASSWORD.value)

        authSuccessful = true

        if (authSuccessful && argHandler.canTryAuthorization()) {
            val resource = argHandler.getArgument(ArgHandler.Arguments.RESOURCE)
            val role = argHandler.getArgument(ArgHandler.Arguments.ROLE)

            if (!validateRole(role))
                exitProcess(ExitCode.UNKNOWN_ROLE.value)

            val resources = listOf(
                UsersResources("A", Role.READ, authService.getUserByLogin("sasha")!!),
                UsersResources("A.AA", Role.WRITE, authService.getUserByLogin("sasha")!!),
                UsersResources("A.AA.AAA", Role.EXECUTE, authService.getUserByLogin("sasha")!!),
                UsersResources("B", Role.EXECUTE, authService.getUserByLogin("admin")!!),
                UsersResources("A.B", Role.WRITE, authService.getUserByLogin("admin")!!),
                UsersResources("A.B", Role.WRITE, authService.getUserByLogin("sasha")!!),
                UsersResources("A.B.C", Role.READ, authService.getUserByLogin("admin")!!),
                UsersResources("A.B.C", Role.WRITE, authService.getUserByLogin("q")!!),
                UsersResources("A.B", Role.EXECUTE, authService.getUserByLogin("q")!!),
                UsersResources("B", Role.READ, authService.getUserByLogin("q")!!),
                UsersResources("A.AA.AAA", Role.READ, authService.getUserByLogin("q")!!),
                UsersResources("A", Role.EXECUTE, authService.getUserByLogin("q")!!),
                UsersResources("A", Role.WRITE, authService.getUserByLogin("admin")!!),
                UsersResources("A.AA", Role.EXECUTE, authService.getUserByLogin("admin")!!),
                UsersResources("B", Role.WRITE, authService.getUserByLogin("sasha")!!),
                UsersResources("A.B", Role.EXECUTE, authService.getUserByLogin("sasha")!!),
                UsersResources("A.B.C", Role.EXECUTE, authService.getUserByLogin("sasha")!!)
            ).filter { it.user == currentUser }

            if (resource != null && role != null && !haveAccess(resources, resource, role))
                exitProcess(ExitCode.NO_ACCESS.value)

            authorizationSuccessful = true
        }
    }

    if (argHandler.shouldPrintHelp()) {
        printHelp()
        if (!authSuccessful)
            exitProcess(ExitCode.HELP.value)
    }
}


fun validateLogin(login: String?) = login != null && login.length <= 10 && login.all { it.isLowerCase() }
fun validatePass(pass: String?) = pass != null && pass.isNotEmpty()


//Отфильтрованные ресурсы для конкретного пользователя
fun haveAccess(resources: List<UsersResources>, res: String, role: String): Boolean {
    return resources.filter { it.path == res }.any { it.role.name == role }
}

fun validateRole(role: String?) = role != null && listOf("READ", "WRITE", "EXECUTE").contains(role)

fun printHelp() {
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

enum class ExitCode(val value: Int) {
    HELP(1),
    INVALID_LOGIN_FORMAT(2),
    UNKNOWN_LOGIN(3),
    INVALID_PASSWORD(4),
    UNKNOWN_ROLE(5),
    NO_ACCESS(6)
}
