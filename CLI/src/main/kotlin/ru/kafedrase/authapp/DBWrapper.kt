package ru.kafedrase.authapp

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import org.flywaydb.core.Flyway

object DBWrapper {
    private val dbSource: HikariDataSource by lazy { initPool() }

    fun initDB() {
        Flyway.configure()
            .dataSource(
                System.getenv("AuthAppDB_URL"),
                System.getenv("AuthAppDB_USER"),
                System.getenv("AuthAppDB_PASS")
            )
            .load()
            .migrate()
    }

    fun getConnection(): Connection = dbSource.connection

    fun closePool() = dbSource.close()

    private fun initPool(): HikariDataSource {
        val source = HikariDataSource()
        source.jdbcUrl = System.getenv("AuthAppDB_URL")
        source.username = System.getenv("AuthAppDB_USER")
        source.password = System.getenv("AuthAppDB_PASS")
        source.maximumPoolSize = 5
        return source
    }
}
