package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.dao.AccountingDAO
import ru.kafedrase.authapp.domain.UserSession

class AccountingService(private val accountingDAO: AccountingDAO) {
    fun write(session: UserSession) {
        accountingDAO.insert(session)
    }
}
