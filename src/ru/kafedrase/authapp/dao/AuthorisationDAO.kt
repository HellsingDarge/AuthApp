package ru.kafedrase.authapp.dao

import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.UsersResources
import java.sql.Connection

class AuthorisationDAO(private val dbConnection: Connection) {
    fun getResourcesByUserLogin(login: String): List<UsersResources> {
        // todo change in schema tables user to login
        val query = "SELECT * FROM Resources WHERE user = ?"
        val statement = dbConnection.prepareStatement(query)
        statement.setString(1, login)
        val result = statement.executeQuery()

        val out = arrayListOf<UsersResources>()

        while (result.next()) {
            out.add(
                UsersResources(
                    result.getString("resource"),
                    Role.valueOf(result.getString("role")),
                    result.getString("user")
                )
            )
        }

        statement.close()
        return out.toList()
    }
}