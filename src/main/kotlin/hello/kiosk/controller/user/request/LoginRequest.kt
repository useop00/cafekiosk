package hello.kiosk.controller.user.request

import hello.kiosk.service.user.request.LoginServiceRequest
import jakarta.validation.constraints.NotEmpty

data class LoginRequest(

    @NotEmpty(message = "아이디를 입력해주세요.")
    val username: String,
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    val password: String
){
    fun toServiceRequest(): LoginServiceRequest {
        return LoginServiceRequest(
            username = username,
            password = password
        )
    }
}
