package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.User
import java.security.MessageDigest

class AuthenticationService(private val userRepository: UserRepository) {
    lateinit var currentUser: User

    fun start(login: String, pass: String): Pair<User?, AuthenticationResultType> {
        currentUser = userRepository.getUserByLogin(login) ?: return Pair(null, AuthenticationResultType.UNKNOWN_LOGIN)

        if (!verifyPass(pass)) return Pair(null, AuthenticationResultType.INVALID_PASSWORD)

        return Pair(currentUser, AuthenticationResultType.SUCCESS)
    }

    private fun verifyPass(pass: String) = currentUser.hash == generateHash(pass, currentUser.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })

}