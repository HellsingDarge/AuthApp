package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.UserSession
import java.sql.Connection
import java.sql.Date

class AccountingService(private val dbConnection: Connection) {
    fun write(session: UserSession) {
        // fixme
        var query = "SELECT id FROM Resources WHERE resource = ? AND role = ? AND user = ?"

        var statement = dbConnection.prepareStatement(query)
        statement.use {
            statement.setString(1, session.resource)
            statement.setString(2, session.role.name)
            statement.setString(3, session.user.login)

            val resourceIdResult = statement.executeQuery()

            resourceIdResult.next()
            val resourceId = resourceIdResult.getInt("id")

            query = "INSERT INTO Sessions(user, resource_id, date_start, date_end, volume) VALUES(?, $resourceId, ?, ?, ? )"

            statement = dbConnection.prepareStatement(query)
            statement.setString(1, session.user.login)
            statement.setDate(2, Date.valueOf(session.dateStart))
            statement.setDate(3, Date.valueOf(session.dateEnd))
            statement.setInt(4, session.volume)

            val result = statement.executeUpdate()

            println("Affected row: $result")
        }
    }
}