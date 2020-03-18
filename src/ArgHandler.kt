import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class ArgHandler(args: Array<String>) {
    val login: String?
    val password: String?
    val resource: String?
    val role: String?
    val dateStart: String?
    val dateEnd: String?
    val volume: String?

    init {
        val parser = ArgParser("app.jar", true)
        val login by parser.option(
            ArgType.String,
            "login",
            "login",
            "Логин пользователя, строка, строчные буквы. Не более 10 символов"
        )
        val password by parser.option(
            ArgType.String,
            "password",
            "pass",
            "Пароль"
        )
        val resource by parser.option(
            ArgType.String,
            "resource",
            "res",
            "Абсолютный путь до запрашиваемого ресурса, формат A.B.C"
        )
        val role by parser.option(
            ArgType.String,
            "role",
            "role",
            "Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE"
        )
        val dateStart by parser.option(
            ArgType.String,
            "date_start",
            "ds",
            "Дата начала сессии с ресурсом, формат YYYY-MM-DD "
        )
        val dateEnd by parser.option(
            ArgType.String,
            "date_end",
            "de",
            "Дата окончания сессии с ресурсом, формат YYYY-MM-DD "
        )
        val volume by parser.option(
            ArgType.String,
            "volume",
            "vol",
            "Потребляемый объем, целое число"
        )
        parser.parse(args)

        this.login = login
        this.password = password
        this.resource = resource
        this.role = role
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.volume = volume
    }

    fun canTryAuthentication() = args.contains(Arguments.LOGIN.value) && args.contains(Arguments.PASSWORD.value)

    fun canTryAuthorization() = args.contains(Arguments.RESOURCE.value) && args.contains(Arguments.ROLE.value)

    fun canTryAccounting() =
        args.contains(Arguments.DATE_START.value) && args.contains(Arguments.DATE_END.value) && args.contains(Arguments.VOLUME.value)

    fun getArgument(arg: Arguments) = tryGetArg(args.indexOf(arg.value))

    fun shouldPrintHelp() = args.isEmpty() || args.contains("-h")

    private fun tryGetArg(index: Int) = args.getOrNull(index + 1)
}
