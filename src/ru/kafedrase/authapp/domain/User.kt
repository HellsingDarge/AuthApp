package ru.kafedrase.authapp.domain

data class User(val login: String, val hash: String, val salt: String)
