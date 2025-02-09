package hello.kiosk.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(
    private val jwtProvider: JwtProvider
) : HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)

        if (token.isNullOrEmpty() || !jwtProvider.isValid(token)) {
            return unauthorized(response)
        }

        request.setAttribute("username", jwtProvider.getUsername(token))
        return true
    }

    private fun unauthorized(response: HttpServletResponse): Boolean {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        return false
    }
}