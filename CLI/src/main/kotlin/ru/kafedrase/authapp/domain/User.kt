package ru.kafedrase.authapp.domain

import com.google.gson.annotations.Expose

data class User(
    @Expose
    val login: String,
    val hash: String,
    val salt: String
)
