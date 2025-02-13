package hello.kiosk.controller.user.request

import hello.kiosk.service.user.request.SignServiceRequest
import jakarta.validation.constraints.NotEmpty

data class SignRequest(

    @field:NotEmpty(message = "아이디를 입력해주세요.")
    val username: String?,

    @field:NotEmpty(message = "비밀번호를 입력해주세요.")
    val password: String?
) {
    fun toServiceRequest(): SignServiceRequest {
        return SignServiceRequest(
            username = username!!,
            password = password!!
        )
    }
}
