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
            shortName = "login",
            description = "Логин пользователя, строка, строчные буквы. Не более 10 символов"
        )
        val password by parser.option(
            ArgType.String,
            shortName = "pass",
            description = "Пароль"
        )
        val resource by parser.option(
            ArgType.String,
            shortName = "res",
            description = "Абсолютный путь до запрашиваемого ресурса, формат A.B.C"
        )
        val role by parser.option(
            ArgType.String,
            shortName = "role",
            description = "Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE"
        )
        val dateStart by parser.option(
            ArgType.String,
            shortName = "ds",
            description = "Дата начала сессии с ресурсом, формат YYYY-MM-DD "
        )
        val dateEnd by parser.option(
            ArgType.String,
            shortName = "de",
            description = "Дата окончания сессии с ресурсом, формат YYYY-MM-DD "
        )
        val volume by parser.option(
            ArgType.String,
            shortName = "vol",
            description = "Потребляемый объем, целое число"
        )
        try {
            parser.parse(args)
        } catch (ex: IllegalStateException) {
        }

        this.login = login
        this.password = password
        this.resource = resource
        this.role = role
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.volume = volume
    }

    fun canAuthentication() = !(login.isNullOrEmpty() || password.isNullOrEmpty())

    fun canAuthorization() = !(resource.isNullOrEmpty() || role.isNullOrEmpty())

    fun canAccounting(): Boolean {
        return !(dateStart.isNullOrEmpty() || dateEnd.isNullOrEmpty() || volume == null)
    }

    fun shouldPrintHelp() = !canAuthentication()
}
