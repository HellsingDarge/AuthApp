package services

import domain.UserSession

class AccountingService {
    private val userSessions = arrayListOf<UserSession>()

    fun write(session: UserSession) = userSessions.add(session)
}