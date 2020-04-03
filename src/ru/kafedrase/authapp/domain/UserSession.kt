package ru.kafedrase.authapp.domain

import ru.kafedrase.authapp.Role
import java.time.LocalDate

data class UserSession(
    val user: User,
    val role: Role,
    val resource: String,
    val dateStart: LocalDate,
    val dateEnd: LocalDate,
    val volume: Int
)