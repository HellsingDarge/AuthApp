package ru.kafedrase.authapp.dao

import java.sql.Connection
import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.UsersResources

class AuthorisationDAO(private val dbConnection: Connection) {
    fun getResource(login: String, role: Role, nodes: List<String>): UsersResources? {
        // todo simplify logic?
        //  instead of just sending all possible combinations, somehow make it so it's not just reiteration
        //  of all possible parent accesses?
        val builder = StringBuilder()

        builder.append(
                "SELECT * FROM Resources WHERE login = ? AND role = '${role.name}' AND (resource = ?"
        )

        for (index in 1 until nodes.size) { // -1 because one "resource = ?" is already in query
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
                // +2 because prepared statement numeration starts from 1 and 1st is already taken
            }

            val result = it.executeQuery()
            if (result.next()) {
                out = UsersResources(
                        result.getInt("id"),
                        result.getString("resource"),
                        Role.valueOf(result.getString("role")),
                        result.getString("login")
                )
            }
        }
        return out
    }

    fun getAllResources(login: String): List<UsersResources> {
        val query = "SELECT * FROM Resources WHERE login = ?"
        val statement = dbConnection.prepareStatement(query)
        val out = mutableListOf<UsersResources>()

        statement.use {
            it.setString(1, login)
            val result = it.executeQuery()

            while (result.next()) {
                out.add(
                    UsersResources(
                        result.getInt("id"),
                        result.getString("resource"),
                        Role.valueOf(result.getString("role")),
                        login
                    )
                )
            }
        }
        return out.toList()
    }

    fun getAllResourcesOfAll(): List<UsersResources> {
        val query = "SELECT * FROM Resources"
        val statement = dbConnection.createStatement()
        val out = mutableListOf<UsersResources>()

        statement.use {
            val result = it.executeQuery(query)

            while (result.next()) {
                out.add(
                    UsersResources(
                        result.getInt("id"),
                        result.getString("resource"),
                        Role.valueOf(result.getString("role")),
                        result.getString("login")
                    )
                )
            }
        }
        return out.toList()
    }

    fun getResourceById(id: Int): UsersResources? {

        val query = "SELECT * FROM Resources WHERE id = $id"
        val statement = dbConnection.createStatement()
        var resource: UsersResources? = null

        statement.use {
            val result = it.executeQuery(query)

            while (result.next()) {
                resource = UsersResources(
                    id,
                    result.getString("resource"),
                    Role.valueOf(result.getString("role")),
                    result.getString("login")
                )
            }
        }
        return resource
    }
}
