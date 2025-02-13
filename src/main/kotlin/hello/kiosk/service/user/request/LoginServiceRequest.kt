package hello.kiosk.service.user.request

import jakarta.validation.constraints.NotEmpty

data class LoginServiceRequest(

    val username: String,
    val password: String
)
