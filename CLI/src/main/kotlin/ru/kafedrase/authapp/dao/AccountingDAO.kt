package ru.kafedrase.authapp.dao

import java.sql.Connection
import java.sql.Date
import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.User
import ru.kafedrase.authapp.domain.UserSession

class AccountingDAO(private val dbConnection: Connection) {
    fun insert(session: UserSession) {
        val query =
            "INSERT INTO Sessions(login, resource_id, date_start, date_end, volume) VALUES(?, ${session.resourceId}, ?, ?, ? )"
        val statement = dbConnection.prepareStatement(query)

        statement.use {
            it.setString(1, session.user.login)
            it.setDate(2, Date.valueOf(session.dateStart))
            it.setDate(3, Date.valueOf(session.dateEnd))
            it.setInt(4, session.volume)
            it.executeUpdate()
        }
    }

    fun getAllActivities(): List<UserSession> {
        val query = "SELECT login, resource_id, date_start, date_end, volume FROM Sessions"
        val statement = dbConnection.createStatement()
        val sessions = mutableListOf<UserSession>()

        statement.use {
            val result = it.executeQuery(query)

            while (result.next()) {
                sessions.add(
                    UserSession(
                        User(result.getString("login"), "", ""),
                        result.getInt("resource_id"),
                        result.getDate("date_start").toLocalDate(),
                        result.getDate("date_end").toLocalDate(),
                        result.getInt("volume")
                    )
                )
            }
        }

        return sessions.toList()
    }

    fun getActivity(id: Int): UserSession? {
        val query = "SELECT * FROM Sessions WHERE id = $id"
        val statement = dbConnection.createStatement()
        var session: UserSession? = null

        statement.use {
            val result = it.executeQuery(query)

            if (result.next()) {
                session = UserSession(
                    User(result.getString("login"), "", ""),
                    id,
                    result.getDate("date_start").toLocalDate(),
                    result.getDate("date_end").toLocalDate(),
                    result.getInt("volume")
                )
            }
        }

        return session
    }

    fun getActivities(role: Role): List<UserSession> {
        val query = "SELECT Sessions.* FROM Sessions INNER JOIN Sessions ON UsersResource.id = Sessions.resource_id WHERE UsersResources.role = $role "
        val statement = dbConnection.createStatement()
        val sessions = mutableListOf<UserSession>()

        statement.use {
            val result = it.executeQuery(query)

            while (result.next()) {
                sessions.add(
                    UserSession(
                        User(result.getString("login"), "", ""),
                        result.getInt("resource_id"),
                        result.getDate("date_start").toLocalDate(),
                        result.getDate("date_end").toLocalDate(),
                        result.getInt("volume")
                    )
                )
            }
        }

        return sessions.toList()
    }
}
