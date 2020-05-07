package ru.kafedrase.authapp

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.matcher.Matchers
import com.google.inject.servlet.GuiceServletContextListener
import com.google.inject.servlet.ServletModule
import javax.servlet.annotation.WebListener
import ru.kafedrase.authapp.injectors.Log4j2TypeListener

@WebListener
class GuiceConfig : GuiceServletContextListener() {
    override fun getInjector(): Injector = Guice.createInjector(object : ServletModule() {
        override fun configureServlets() {
            bindListener(Matchers.any(), Log4j2TypeListener())

            serve("/echo/post").with(PostListener::class.java)
            serve("/echo/get").with(GetListener::class.java)

            serve("/ajax/user").with(UserServlet::class.java)
            serve("/ajax/authority").with(AuthorityServlet::class.java)
            serve("/ajax/activity").with(ActivityServlet::class.java)
        }
    })
}
