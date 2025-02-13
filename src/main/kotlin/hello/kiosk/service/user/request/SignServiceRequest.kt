package hello.kiosk.service.user.request

import hello.kiosk.domain.user.User
import jakarta.validation.constraints.NotEmpty

data class SignServiceRequest(

    val username: String,
    val password: String
) {
    fun toEntity(hashedPassword: String, salt: String): User {
        return User.create(
            username = username,
            password = hashedPassword,
            salt = salt
        )
    }
}
