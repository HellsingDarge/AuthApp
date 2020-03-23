package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.Role
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserService {
    fun isLoginValid(login: String?) = !login.isNullOrBlank() && login.matches(Regex("[a-z]{1,10}"))

    fun isRoleValid(role: String?) = !role.isNullOrBlank() && Role.getNames().contains(role)

    fun parseDate(date: String): Date? = try {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.isLenient = false
        formatter.parse(date)
    } catch (ex: ParseException) {
        null
    }

    fun parseVolume(volume: String): Int? {
        return try {
            volume.toInt()
        } catch (ex: NumberFormatException) {
            return null
        }
    }

    fun areDatesValid(start: Date?, end: Date?) = start != null && end != null && end.after(start)

    fun isVolumeValid(volume: Int?)= volume != null && volume > 0
}