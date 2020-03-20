package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.User
import java.security.MessageDigest

class AuthenticationService(private val userRepository: UserRepository) {
    class UnknownLogin : Throwable()
    class InvalidPassword : Throwable()

    fun start(login: String, pass: String): User {
        val user = userRepository.getUserByLogin(login) ?: throw UnknownLogin()

        if (!verifyPass(user, pass)) throw InvalidPassword()

        return user
    }

    private fun verifyPass(user: User, pass: String) = user.hash == generateHash(pass, user.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })

}

