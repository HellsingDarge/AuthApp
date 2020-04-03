package ru.kafedrase.authapp.dao

import ru.kafedrase.authapp.domain.User
import java.sql.Connection

class AuthenticationDAO(private val dbConnection: Connection) {
    fun getUserByLogin(login: String): User? {
        val query = "SELECT * FROM UsersCredentials WHERE login = ?"
        val statement = dbConnection.prepareStatement(query)
        statement.setString(1, login)
        val result = statement.executeQuery()
        var user: User? = null
        if (result.next()) {
            user = User(
                result.getString("login"),
                result.getString("hash"),
                result.getString("salt")
            )
        }

        statement.close()
        return user
    }
}