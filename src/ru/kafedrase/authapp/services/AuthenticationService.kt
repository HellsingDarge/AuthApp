package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.User
import java.security.MessageDigest

class AuthenticationService(private val userRepository: UserRepository) {
    lateinit var currentUser: User

    fun isExistUser(login: String): Boolean {
        currentUser = userRepository.getUserByLogin(login) ?: return false
        return true
    }

    fun verifyPass(pass: String) = currentUser.hash == generateHash(pass, currentUser.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })

}