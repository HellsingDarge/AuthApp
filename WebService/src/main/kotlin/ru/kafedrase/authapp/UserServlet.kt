package ru.kafedrase.authapp

import com.google.inject.Singleton
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServlet

@Singleton
class UserListener : HttpServlet() {
    override fun service(req: ServletRequest?, res: ServletResponse?) {
        super.service(req, res)
    }
}