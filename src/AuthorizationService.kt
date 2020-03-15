class AuthorizationService(val usersResource: UsersResources) {

    private var resources: List<UsersResources> = listOf(
        UsersResources(path = "A", role = Role.READ, login = "sasha"),
        UsersResources(path = "A.AA", role = Role.WRITE, login = "sasha"),
        UsersResources(path = "A.AA.AAA", role = Role.EXECUTE, login = "sasha"),
        UsersResources(path = "B", role = Role.EXECUTE, login = "admin"),
        UsersResources(path = "A.B", role = Role.WRITE, login = "admin"),
        UsersResources(path = "A.B", role = Role.WRITE, login = "sasha"),
        UsersResources(path = "A.B.C", role = Role.READ, login = "admin"),
        UsersResources(path = "A.B.C", role = Role.WRITE, login = "q"),
        UsersResources(path = "A.B", role = Role.EXECUTE, login = "q"),
        UsersResources(path = "B", role = Role.READ, login = "q"),
        UsersResources(path = "A.AA.AAA", role = Role.READ, login = "q"),
        UsersResources(path = "A", role = Role.EXECUTE, login = "q"),
        UsersResources(path = "A", role = Role.WRITE, login = "admin"),
        UsersResources(path = "A.AA", role = Role.EXECUTE, login = "admin"),
        UsersResources(path = "B", role = Role.WRITE, login = "sasha"),
        UsersResources(path = "A.B", role = Role.EXECUTE, login = "sasha"),
        UsersResources(path = "A.B.C", role = Role.EXECUTE, login = "sasha")
    )

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан (Например для A.AA.AA - Read)
     * Иначе, последовательно ищем от корня дерева подходящий доступ до прямого родителя (A - READ и A.AA - READ)
     */
    fun haveAccess(): Boolean {
        if (haveResourceWithRole(resources = resources, res = usersResource.path, role = usersResource.role)) {
            return true
        }
        val nodesOfResources = usersResource.path.split(".")
        var currentNode = nodesOfResources.first()
        nodesOfResources.dropLast(1)
        for (index in nodesOfResources.indices) {
            if (haveResourceWithRole(resources = resources, res = currentNode, role = usersResource.role)) {
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