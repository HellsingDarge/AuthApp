package ru.kafedrase.authapp.domain

import ru.kafedrase.authapp.Role

data class UsersResources(val id: Int, val path: String, val role: Role, val login: String)