package ru.kafedrase.authapp

import ru.kafedrase.authapp.domain.User
import ru.kafedrase.authapp.domain.UsersResources
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val app = Application(args)
    val returnCode = app.run()
    exitProcess(returnCode.value)
}

var resources: List<UsersResources> = listOf(
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

val users = listOf(
    User(
        "sasha",
        "bc4725cd5915a9cda45d2835bdd8e444be15c7c9aabdd0dc8693d7a7d2500dc3",
        "V9Me2nx"
    ),
    User(
        "admin",
        "e0feb157dadff817c0f11b48d0441e56b475a27289117c6cb1ca7fd0b108b13c",
        "6xInN0l"
    ),
    User(
        "q",
        "2002c9e01093b6d8b7d3699d6b7bd1a5fb8200340b1275f52ae5794d59854849",
        "4bxdOU7"
    ),
    User(
        "abcdefghij",
        "d880929e469c4a2c19352f76460853be52ee581f7fcdd3097f86f670f690e910",
        "TM36tOy"
    )
)