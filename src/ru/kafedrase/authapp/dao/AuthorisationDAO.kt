package ru.kafedrase.authapp.dao

import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.UsersResources
import java.sql.Connection

class AuthorisationDAO(private val dbConnection: Connection) {
    fun getResource(login: String, role: Role, nodes: List<String>): UsersResources? {
        // todo change in schema tables user to login
        val builder = StringBuilder()
        builder.append(
                "SELECT * FROM Resources WHERE  user = ? AND role = '${role.name}' AND (resource = ?"
        )
        for (index in 1 until nodes.size) { //-1 because one "resource = ?" is already in query
            builder.append(" OR resource = ?")
        }
        builder.append(")")
        val query = builder.toString()

        val statement = dbConnection.prepareStatement(query)

        var out: UsersResources? = null

        statement.use {
            it.setString(1, login)
            for (index in nodes.indices) {
                val currentNode = nodes.subList(0, index + 1).joinToString(".")
                it.setString(index + 2, currentNode)
                //+2 because prepared statement numeration starts from 1 and 1st is already taken
            }

            val result = statement.executeQuery()
            if (result.next()) {
                out = UsersResources(
                        result.getInt("id"),
                        result.getString("resource"),
                        Role.valueOf(result.getString("role")),
                        result.getString("user")
                )

            }
        }
        return out
    }
}