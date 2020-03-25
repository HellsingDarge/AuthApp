package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.domain.UsersResources

class AuthorizationService(val usersResource: UsersResources, private var resourceRepository: ResourceRepository) {

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    fun haveAccess(): Boolean {
        val resources = resourceRepository.getResourcesByUserLogin(usersResource.login)

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