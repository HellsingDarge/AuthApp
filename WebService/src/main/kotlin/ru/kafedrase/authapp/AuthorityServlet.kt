package ru.kafedrase.authapp

import com.google.inject.Inject
import com.google.inject.Singleton
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.sql.DataSource
import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.injectors.GsonProvider

@Singleton
class AuthorityServlet : HttpServlet() {
    @Inject
    lateinit var gson: GsonProvider

    @Inject
    lateinit var dataSourceProvider: DataSource

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        if (req.queryString == null) {
            doWithConnection(resp) { authorisationDAO ->
                authorisationDAO.getAllResourcesOfAll()
            }
            return
        }

        val id = req.getParameter("id")?.toIntOrNull()
        if (id != null && id >= 1) {
            doWithConnection(resp) { authorisationDAO ->
                authorisationDAO.getResourceById(id)
            }
            return
        }

        val userId = req.getParameter("userId")
        if (!userId.isNullOrBlank()) {
            doWithConnection(resp) { authorisationDAO ->
                val resources = authorisationDAO.getAllResources(userId)
                if (resources.isEmpty()) null
                else resources
            }
            return
        }

        resp.sendError(SC_NOT_FOUND)
    }

    private fun doWithConnection(resp: HttpServletResponse, body: (AuthorisationDAO) -> Any?) {
        dataSourceProvider.connection.use { connection ->
            val authorisationDAO = AuthorisationDAO(connection)
            val result = body(authorisationDAO)

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
