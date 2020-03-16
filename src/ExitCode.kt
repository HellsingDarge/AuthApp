enum class ExitCode(val value: Int) {
    SUCCESS(0),
    HELP(1),
    INVALID_LOGIN_FORMAT(2),
    UNKNOWN_LOGIN(3),
    INVALID_PASSWORD(4),
    UNKNOWN_ROLE(5),
    NO_ACCESS(6),
    INVALID_ACTIVITY(7)
}
