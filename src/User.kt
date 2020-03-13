data class User(val login: String, val pass: String) {

    private val _users = mapOf(
        "sasha" to "123",
        "admin" to "qwerty",
        "q" to """@#$%^&*!""",
        "abcdefghij" to "abc"
    )

    fun exists() = _users.containsKey(login)
    fun verifyPass() = _users[login] == pass
}