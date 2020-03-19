package services

import Role
import domain.UsersResources

class AuthorizationService(private val usersResource: UsersResources, private var resources: List<UsersResources>) {

    lateinit var availableResources: UsersResources

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    fun haveAccess(): Boolean {
        if (usersResource.path == null)
            return false

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

    init {
        resources = resources.filter { it.login == usersResource.login }
    }

}