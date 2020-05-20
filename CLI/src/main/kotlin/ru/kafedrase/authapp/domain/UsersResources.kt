package ru.kafedrase.authapp.domain

import com.google.gson.annotations.Expose
import ru.kafedrase.authapp.Role

data class UsersResources(
    @Expose
    val id: Int,
    @Expose
    val path: String,
    val role: Role,
    @Expose
    val login: String
)
