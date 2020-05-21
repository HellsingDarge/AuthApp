package ru.kafedrase.authapp

import com.google.inject.Inject
import com.google.inject.Singleton
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.sql.DataSource
import org.apache.logging.log4j.Logger
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.injectors.GsonProvider
import ru.kafedrase.authapp.injectors.InjectLogger

@Singleton
class UserServlet : HttpServlet() {
    @InjectLogger
    lateinit var log: Logger
    @Inject
    lateinit var gson: GsonProvider
    @Inject
    lateinit var dataSourceProvider: DataSource

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        log.trace("Query string: ${req.queryString}")

        if (req.queryString == null) {
            doWithConnection(resp) { authenticationDAO ->
                authenticationDAO.getAllUsers()
            }
            return
        }

        val login = req.getParameter("id")
        if (!login.isNullOrBlank()) {
            doWithConnection(resp) { authenticationDAO ->
                authenticationDAO.getUserByLogin(login)
            }
            return
        }

        resp.sendError(SC_NOT_FOUND)
    }

    private fun doWithConnection(resp: HttpServletResponse, body: (AuthenticationDAO) -> Any?) {
        dataSourceProvider.connection.use { connection ->
            val authenticationDAO = AuthenticationDAO(connection)
            val result = body(authenticationDAO)

            if (result == null) {
                resp.sendError(SC_NOT_FOUND)
            } else {
                val json = gson.get()
                resp.contentType = "application/json"
                resp.writer.print(json.toJson(result))
            }
        }
    }
}
