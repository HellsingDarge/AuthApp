import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

@WebServlet(name = "EchoListener", urlPatterns = ["echo/*"], loadOnStartup = 1)
class EchoListener : HttpServlet() {
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.sendError(SC_NOT_FOUND)
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.sendError(SC_NOT_FOUND)
    }
}

@WebServlet(name = "PostListened", urlPatterns = ["echo/post"], loadOnStartup = 1)
class PostListener : HttpServlet() {
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id-input")
        resp.sendRedirect("get?id=$id")
    }
}

@WebServlet(name = "GetListened", urlPatterns = ["echo/get"], loadOnStartup = 1)
class GetListener : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")
        req.setAttribute("id", id)
        req.getRequestDispatcher("/response.jsp").forward(req, resp)
    }
}
