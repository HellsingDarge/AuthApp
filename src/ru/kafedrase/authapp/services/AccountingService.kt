package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.UserSession

class AccountingService {
    private val userSessions = arrayListOf<UserSession>()

    fun write(session: UserSession) = userSessions.add(session)
}