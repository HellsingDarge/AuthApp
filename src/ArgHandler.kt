class ArgHandler(private val args: Array<String>) {
    enum class Arguments(val value: String) {
        LOGIN("-login"),
        PASSWORD("-pass"),
        RESOURCE("-res"),
        ROLE("-role"),
        DATE_START("-ds"),
        DATE_END("-de"),
        VOLUME("-vol")
    }

    fun canTryAuthentication() = args.contains(Arguments.LOGIN.value) && args.contains(Arguments.PASSWORD.value)

    fun canTryAuthorization() = args.contains(Arguments.RESOURCE.value) && args.contains(Arguments.ROLE.value)

    fun canTryAccounting() =
        args.contains(Arguments.DATE_START.value) && args.contains(Arguments.DATE_END.value) && args.contains(Arguments.VOLUME.value)

    fun getArgument(arg: Arguments) = tryGetArg(args.indexOf(arg.value))

    private fun tryGetArg(index: Int) = args.getOrNull(index + 1)
}
