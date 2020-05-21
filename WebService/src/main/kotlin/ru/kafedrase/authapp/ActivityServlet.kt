package ru.kafedrase.authapp

import com.google.inject.Inject
import com.google.inject.Singleton
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.sql.DataSource
import org.apache.logging.log4j.Logger
import ru.kafedrase.authapp.dao.AccountingDAO
import ru.kafedrase.authapp.injectors.GsonProvider
import ru.kafedrase.authapp.injectors.InjectLogger

@Singleton
class ActivityServlet : HttpServlet() {
    @InjectLogger
    lateinit var log: Logger
    @Inject
    lateinit var gson: GsonProvider
    @Inject
    lateinit var dataSourceProvider: DataSource

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        log.trace("Query string: ${req.queryString}")
        if (req.queryString == null) {
            doWithConnection(resp) { accountingDAO ->
                accountingDAO.getAllActivities()
            }
            return
        }

        val id = req.getParameter("id")?.toIntOrNull()
        if (id != null && id >= 1) {
            doWithConnection(resp) { accountingDAO ->
                accountingDAO.getActivity(id)
            }
            return
        }

        val role = req.getParameter("authorityId") ?: return
        if (Role.getNames().contains(role)) {
            doWithConnection(resp) { accountingDAO ->
                accountingDAO.getActivities(Role.valueOf(role))
            }
            return
        }

        resp.sendError(SC_NOT_FOUND)
    }

    private fun doWithConnection(resp: HttpServletResponse, body: (AccountingDAO) -> Any?) {
        dataSourceProvider.connection.use { connection ->
            val accountingDAO = AccountingDAO(connection)
            val result = body(accountingDAO)

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
