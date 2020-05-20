package ru.kafedrase.authapp

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.inject.Provider
import javax.sql.DataSource

class DataSourceProvider : Provider<DataSource> {
    override fun get(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = System.getenv("AuthAppDB_URL")
        config.username = System.getenv("AuthAppDB_USER")
        config.password = System.getenv("AuthAppDB_PASS")
        return HikariDataSource(config)
    }
}

class DataSourceModule : AbstractModule() {
    override fun configure() {
        bind(DataSource::class.java).toProvider(DataSourceProvider::class.java).`in`(Scopes.SINGLETON)
    }
}
