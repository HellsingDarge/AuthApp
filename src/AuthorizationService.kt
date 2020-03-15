class AuthorizationService {

    constructor(usersResource: UsersResources) {
        this.resources = listOf(
            UsersResources("A", Role.READ, "sasha"),
            UsersResources("A.AA", Role.WRITE, "sasha"),
            UsersResources("A.AA.AAA", Role.EXECUTE, "sasha"),
            UsersResources("B", Role.EXECUTE, "admin"),
            UsersResources("A.B", Role.WRITE, "admin"),
            UsersResources("A.B", Role.WRITE, "sasha"),
            UsersResources("A.B.C", Role.READ, "admin"),
            UsersResources("A.B.C", Role.WRITE, "q"),
            UsersResources("A.B", Role.EXECUTE, "q"),
            UsersResources("B", Role.READ, "q"),
            UsersResources("A.AA.AAA", Role.READ, "q"),
            UsersResources("A", Role.EXECUTE, "q"),
            UsersResources("A", Role.WRITE, "admin"),
            UsersResources("A.AA", Role.EXECUTE, "admin"),
            UsersResources("B", Role.WRITE, "sasha"),
            UsersResources("A.B", Role.EXECUTE, "sasha"),
            UsersResources("A.B.C", Role.EXECUTE, "sasha")
        )
        resources = resources.filter { it.login == usersResource.login }
        this.usersResource = usersResource
    }

    private var resources: List<UsersResources>
    val usersResource: UsersResources

    /**
     * Работает для отфильтрованных ресурсов по конкретному пользователю
     * Если найдено прямое совпадение ресурса и роли — доступ найден и выдан
     * Иначе, последовательно ищем от корня дерева подходящий доступ
     */
    fun haveAccess(): Boolean {

        if (isFoundedResourceWithRole(resources, usersResource.path, usersResource.role)) {
            return true
        }
        val nodesOfResources = usersResource.path.split(".")
        var currentNode = nodesOfResources.first()

        for (index in nodesOfResources.indices) {
            if (isFoundedResourceWithRole(resources, currentNode, usersResource.role)) {
                return true
            } else {
                val childNode = nodesOfResources.getOrNull(index) ?: return false
                currentNode = "$currentNode.$childNode"
            }
        }
        return false
    }

    private fun isFoundedResourceWithRole(
        resources: List<UsersResources>,
        res: String,
        role: Role
    ) = resources.filter { it.path == res }.any { it.role == role }

}