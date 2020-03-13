data class ArgHandler(val args: Array<String>) {
    var help: Boolean = false
    var login: String? = null
    var pass: String? = null
    var role: String? = null
    var resource: String? = null

    init {
        for ((index, arg) in args.withIndex()) {
            when (arg) {
                "-h" -> help = true
                "-login" -> login = tryGetArg(index)
                "-pass" -> pass = tryGetArg(index)
                "-role" -> role = tryGetArg(index)
                "-res" -> resource = tryGetArg(index)
            }
        }
    }

    private fun tryGetArg(index: Int) = args.getOrNull(index + 1)
}