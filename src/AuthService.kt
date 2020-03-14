import java.security.MessageDigest

class AuthService {
    private val _users = listOf(
        User(
            "sasha",
            "bc4725cd5915a9cda45d2835bdd8e444be15c7c9aabdd0dc8693d7a7d2500dc3",
            "V9Me2nx"
        ),
        User(
            "admin",
            "e0feb157dadff817c0f11b48d0441e56b475a27289117c6cb1ca7fd0b108b13c",
            "6xInN0l"
        ),
        User(
            "q",
            "2002c9e01093b6d8b7d3699d6b7bd1a5fb8200340b1275f52ae5794d59854849",
            "4bxdOU7"
        ),
        User(
            "abcdefghij",
            "d880929e469c4a2c19352f76460853be52ee581f7fcdd3097f86f670f690e910",
            "TM36tOy"
        )
    )

    fun userExists(user: User?) = _users.contains(user)
    fun getUserByLogin(login: String) = _users.find { it.login == login }
    fun verifyPass(pass: String, user: User) = user.hash == generateHash(pass, user.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })

}