package ru.kafedrase.authapp.services

import ru.kafedrase.authapp.Role
import java.text.SimpleDateFormat
import java.util.*

class UserService {
    fun isLoginValid(login: String?) = !login.isNullOrBlank() && login.matches(Regex("[a-z]{1,10}"))

    fun isRoleValid(role: String?) = !role.isNullOrBlank() && Role.getNames().contains(role)

    fun parseDate(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.isLenient = false
        return formatter.parse(date)
    }

    fun parseVolume(volume: String) = volume.toInt()

    fun areDatesValid(s: Date, e: Date) = s.after(e)

    fun isVolumeValid(volume: Int)= volume > 0
}