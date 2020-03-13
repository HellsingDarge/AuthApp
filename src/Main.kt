import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printHelp()
        exitProcess(1)
    }

    if (args[0] == "-h" || args[0].isBlank()) {
        printHelp()
        exitProcess(1)
    }

    if (args.size == 1 && args[0] != "-h") {
        printHelp()
        exitProcess(0)
    }

    if (isAuthNeeded(args)) {
        val login = args[1]
        val pass = args[3]

        if (!validateLogin(login) || !validatePass(pass))
            exitProcess(2)
    }

}

fun isAuthNeeded(args: Array<String>) = args[0] == "-login" && args[2] == "-pass"
fun validateLogin(login: String) = login.length <= 10 && login.all { it.isLowerCase() }
fun validatePass(pass: String) = pass.isNotEmpty()

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