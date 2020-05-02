import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.servlet.GuiceServletContextListener
import com.google.inject.servlet.ServletModule
import javax.servlet.annotation.WebListener

@WebListener
class GuiceConfig : GuiceServletContextListener() {
    override fun getInjector(): Injector = Guice.createInjector(object : ServletModule() {
        override fun configureServlets() {
            super.configureServlets()
            serve("/echo/post").with(PostListener::class.java)
            serve("/echo/get").with(GetListener::class.java)
        }
    })
}
