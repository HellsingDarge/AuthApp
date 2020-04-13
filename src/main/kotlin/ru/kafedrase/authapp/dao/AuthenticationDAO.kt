package ru.kafedrase.authapp.dao

import java.sql.Connection
import ru.kafedrase.authapp.domain.User

class AuthenticationDAO(private val dbConnection: Connection) {
    fun getUserByLogin(login: String): User? {
        val query = "SELECT * FROM UsersCredentials WHERE login = ?"
        val statement = dbConnection.prepareStatement(query)
        var user: User? = null
        statement.use {
            it.setString(1, login)
            val result = statement.executeQuery()
            if (result.next()) {
                user = User(
                        result.getString("login"),
                        result.getString("hash"),
                        result.getString("salt")
                )
            }
        }
        return user
    }
}
