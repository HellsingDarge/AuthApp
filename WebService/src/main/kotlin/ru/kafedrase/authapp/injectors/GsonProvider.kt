package ru.kafedrase.authapp.injectors

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.inject.Provider

class GsonProvider : Provider<Gson> {
    override fun get(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HHmmss")
        .excludeFieldsWithoutExposeAnnotation()
        .create()
}
