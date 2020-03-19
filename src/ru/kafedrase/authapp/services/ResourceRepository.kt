package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.domain.UsersResources

class ResourceRepository {
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
        UsersResources("B", Role.READ, "q"),
        UsersResources("A.AA.AAA", Role.READ, "q"),
        UsersResources("A", Role.EXECUTE, "q"),
        UsersResources("A", Role.WRITE, "admin"),
        UsersResources("A.AA", Role.EXECUTE, "admin"),
        UsersResources("B", Role.WRITE, "sasha"),
        UsersResources("A.B", Role.EXECUTE, "sasha"),
        UsersResources("A.B.C", Role.EXECUTE, "sasha")
    )

    fun getResourcesByUserLogin(login: String) = resources.filter { it.login == login }
}