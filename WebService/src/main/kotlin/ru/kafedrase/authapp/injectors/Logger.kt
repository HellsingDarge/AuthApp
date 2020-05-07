package ru.kafedrase.authapp.injectors

import com.google.inject.MembersInjector
import com.google.inject.TypeLiteral
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import java.lang.reflect.Field
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectLogger

class Log4j2TypeListener : TypeListener {
    override fun <T : Any?> hear(type: TypeLiteral<T>, encounter: TypeEncounter<T>) {
        var cl = type.rawType

        while (cl != null) {
            for (field in cl.declaredFields) {
                if (field.type == Logger::class.java && field.isAnnotationPresent(InjectLogger::class.java)) {
                    encounter.register(Log4J2MembersInjector<T>(field))
                }
            }

            cl = cl.superclass
        }
    }
}

internal class Log4J2MembersInjector<T>(private val field: Field) : MembersInjector<T> {
    private val logger: Logger = LogManager.getLogger(field.declaringClass)

    init {
        field.isAccessible = true
    }

    override fun injectMembers(type: T) {
        try {
            field.set(type, logger)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }
}
