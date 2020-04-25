package ru.kafedrase.authapp.domain

import java.time.LocalDate

data class UserSession(
    val user: User,
    val resourceId: Int,
    val dateStart: LocalDate,
    val dateEnd: LocalDate,
    val volume: Int
)
