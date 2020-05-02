import com.google.inject.Singleton
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class PostListener : HttpServlet() {
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id-input")
        resp.sendRedirect("get?id=$id")
    }
}

@Singleton
class GetListener : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")
        req.setAttribute("id", id)
        req.getRequestDispatcher("/response.jsp").forward(req, resp)
    }
}
