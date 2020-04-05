package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.UserSession
import java.sql.Connection
import java.sql.Date

class AccountingService(private val dbConnection: Connection) {
    fun write(session: UserSession) {

        val query = "INSERT INTO Sessions(user, resource_id, date_start, date_end, volume) VALUES(?, ${session.resourceId}, ?, ?, ? )"
        val statement = dbConnection.prepareStatement(query)

        statement.use {
            it.setString(1, session.user.login)
            it.setDate(2, Date.valueOf(session.dateStart))
            it.setDate(3, Date.valueOf(session.dateEnd))
            it.setInt(4, session.volume)

            val result = it.executeUpdate()

            println("Affected row: $result")
        }
    }
}