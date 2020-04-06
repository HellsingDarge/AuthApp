package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.domain.UsersResources

class AuthorisationService(val usersResource: UsersResources, private val authorisationDAO: AuthorisationDAO) {

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    var resource: UsersResources? = null

    fun haveAccess(): Boolean {
        val nodes = usersResource.path.split(".")
        resource = authorisationDAO.getResource(usersResource.login, usersResource.role, nodes)
        return resource != null
    }
}