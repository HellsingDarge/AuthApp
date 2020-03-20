package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.types.AuthorizationResultType
import kotlin.math.log

class AuthorizationService(private var resourceRepository: ResourceRepository) {

    lateinit var availableResources: UsersResources

    fun start(res: String, role: Role, login: String): Pair<UsersResources?, AuthorizationResultType> {
        val resources = UsersResources(res, role, login)

        return if (haveAccess(resources)) {
            Pair(resources, AuthorizationResultType.SUCCESS)
        } else {
            Pair(null, AuthorizationResultType.NO_ACCESS)
        }
    }

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    private fun haveAccess(usersResource: UsersResources): Boolean {
        val resources = resourceRepository.getResourcesByUserLogin(usersResource.login)

        if (haveResourceWithRole(resources, usersResource.path, usersResource.role)) {
            availableResources = usersResource
            return true
        }

        val nodesOfResources = usersResource.path.split(".")
        var currentNode = nodesOfResources.first()
        nodesOfResources.dropLast(1)
        for (index in nodesOfResources.indices) {
            if (haveResourceWithRole(resources, currentNode, usersResource.role)) {
                availableResources = usersResource
                return true
            } else {
                val childNode = nodesOfResources.getOrNull(index) ?: return false
                currentNode = "$currentNode.$childNode"
            }
        }
        return false
    }

    private fun haveResourceWithRole(
        resources: List<UsersResources>,
        res: String,
        role: Role
    ) = resources.filter { it.path == res }.any { it.role == role }

}