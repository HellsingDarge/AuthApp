class AuthorizationService(val usersResource: UsersResources) {

    private var resources: List<UsersResources> = listOf(
        UsersResources("A", Role.READ, "sasha"),
        UsersResources("A.AA", Role.WRITE, "sasha"),
        UsersResources("A.AA.AAA", Role.EXECUTE, "sasha"),
        UsersResources("B", Role.EXECUTE, "admin"),
        UsersResources("A.B", Role.WRITE, "admin"),
        UsersResources("A.B", Role.WRITE, "sasha"),
        UsersResources("A.B.C", Role.READ, "admin"),
        UsersResources("A.B.C", Role.WRITE, "q"),
        UsersResources("A.B", Role.EXECUTE, "q"),
        UsersResources("B", Role.READ,"q"),
        UsersResources("A.AA.AAA", Role.READ, "q"),
        UsersResources("A", Role.EXECUTE, "q"),
        UsersResources("A", Role.WRITE, "admin"),
        UsersResources("A.AA", Role.EXECUTE, "admin"),
        UsersResources("B", Role.WRITE, "sasha"),
        UsersResources("A.B", Role.EXECUTE, "sasha"),
        UsersResources("A.B.C", Role.EXECUTE, "sasha")
    )

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    fun haveAccess(): Boolean {
        if (haveResourceWithRole(resources,usersResource.path, usersResource.role)) {
            return true
        }
        val nodesOfResources = usersResource.path.split(".")
        var currentNode = nodesOfResources.first()
        nodesOfResources.dropLast(1)
        for (index in nodesOfResources.indices) {
            if (haveResourceWithRole(resources, currentNode,usersResource.role)) {
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