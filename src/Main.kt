import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val argHandler = ArgHandler(args)
    var authSuccessful = false

    if (isAuthNeeded(args)) {
        val login = argHandler.login
        val pass = argHandler.pass

        if (!validateLogin(login))
            exitProcess(2)

        if (pass == null || !validatePass(pass))
            exitProcess(4)

        val currentUser = User(login!!, pass)

        if (!currentUser.exists())
            exitProcess(3)

        if (!currentUser.verifyPass())
            exitProcess(4)

        authSuccessful = true
    }

    if (isHelpNeeded(args)) {
        printHelp()
        if (!authSuccessful)
            exitProcess(1)
    }
}

private fun isHelpNeeded(args: Array<String>): Boolean {
    if (args.isEmpty())
        return true
    if (args.contains("-h"))
        return true
    return false
}

fun isAuthNeeded(args: Array<String>) = args.contains("-pass") && args.contains("-login")
fun validateLogin(login: String?) = login != null && login.length <= 10 && login.all { it.isLowerCase() }
fun validatePass(pass: String?) = pass != null && pass.isNotEmpty()

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