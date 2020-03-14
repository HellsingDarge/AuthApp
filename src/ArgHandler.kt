class ArgHandler(private val args: Array<String>) {
    enum class Arguments(val value: String?) {
        LOGIN("-login"),
        PASSWORD("-pass")
    }

    fun canTryAuth() = args.contains(Arguments.LOGIN.value) && args.contains(Arguments.PASSWORD.value)
    fun shouldPrintHelp() = args.isEmpty() || args.contains("-h")
    fun getArgument(arg: Arguments) = tryGetArg(args.indexOf(arg.value))

    private fun tryGetArg(index: Int) = args.getOrNull(index + 1)
}
