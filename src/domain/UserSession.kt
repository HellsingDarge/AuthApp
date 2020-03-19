package domain

import java.util.*

data class UserSession(
    val user: User, val resource: String,
    val dateStart: Date, val dateEnd: Date, val volume: Int
)