package ru.kafedrase.authapp

import com.google.inject.Singleton
import java.net.URLEncoder
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.logging.log4j.Logger
import ru.kafedrase.authapp.injectors.InjectLogger

@Singleton
class PostListener : HttpServlet() {
    @InjectLogger
    lateinit var log: Logger

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id-input")

        log.trace("Received id: $id")

        val encodedID = URLEncoder.encode(id, "UTF-8")

        log.trace("EncodedID: $encodedID")

        resp.sendRedirect("get?id=$encodedID")
    }
}

@Singleton
class GetListener : HttpServlet() {
    @InjectLogger
    lateinit var log: Logger

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")

        log.trace("Received id: $id")

        req.setAttribute("id", id)
        req.getRequestDispatcher("/response.jsp").forward(req, resp)
    }
}
