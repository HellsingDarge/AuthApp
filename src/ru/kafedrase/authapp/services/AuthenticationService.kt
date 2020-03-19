package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.ExitCode
import ru.kafedrase.authapp.domain.User
import java.security.MessageDigest

class AuthenticationService(private val users: List<User>) {
    lateinit var currentUser: User

    fun start(login: String, pass: String): ExitCode {
        val user = users.find { it.login == login } ?: return ExitCode.UNKNOWN_LOGIN
        if (!verifyPass(pass, user)) return ExitCode.INVALID_PASSWORD

        currentUser = user

        return ExitCode.SUCCESS
    }

    private fun verifyPass(pass: String, user: User) = user.hash == generateHash(pass, user.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })

}