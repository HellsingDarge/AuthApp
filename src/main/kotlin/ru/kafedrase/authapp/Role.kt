package ru.kafedrase.authapp

enum class Role {
    READ, WRITE, EXECUTE;

    companion object {
        fun getNames() = values().map { it.name }
    }
}
