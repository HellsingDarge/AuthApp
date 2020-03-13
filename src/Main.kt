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

    if (args[0] != "-h") {
        printHelp()
        exitProcess(0)
    }
}

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