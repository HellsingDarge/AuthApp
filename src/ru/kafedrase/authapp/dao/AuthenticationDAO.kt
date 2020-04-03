package ru.kafedrase.authapp.dao

import ru.kafedrase.authapp.domain.User
import java.sql.Connection

class AuthenticationDAO(private val dbConnectoion: Connection) {
    fun getUserByLogin(login: String): User? {
        val query = "SELECT * FROM UsersCredentials WHERE login = ?"
        val statement = dbConnectoion.prepareStatement(query)
        statement.setString(1, login)
        val result = statement.executeQuery()

        if (result.next()) {
            return User(
                result.getString("login"),
                result.getString("hash"),
                result.getString("salt")
            )
        }

        return null
    }
}