package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.domain.User
import java.security.MessageDigest

class AuthenticationService(private val authenticationDAO: AuthenticationDAO) {
    lateinit var currentUser: User

    fun start(login: String): Boolean {
        currentUser = authenticationDAO.getUserByLogin(login) ?: return false
        return true
    }

    fun verifyPass(pass: String) = currentUser.hash == generateHash(pass, currentUser.salt)

    private fun generateHash(plaintext: String, salt: String) =
        MessageDigest.getInstance("SHA-256")
            .digest((plaintext + salt).toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
}
