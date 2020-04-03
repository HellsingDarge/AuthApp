package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.domain.UsersResources

class AuthorisationService(val usersResource: UsersResources, private val authorisationDAO: AuthorisationDAO) {

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    fun haveAccess(): Boolean {
        // todo instead of returning all resources for this login, make it only return resources that should be checked?
        val resources = authorisationDAO.getResourcesByUserLogin(usersResource.login)

        if (resources.isEmpty())
            return false

        val nodes = usersResource.path.split(".")

        for (index in nodes.indices) {
            val currentNode = nodes.subList(0, index + 1).joinToString(".")
            if (resources.any { it.path == currentNode && it.role == usersResource.role })
                return true
        }

        return false
    }
}