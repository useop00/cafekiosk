package hello.kiosk.service.user.response

import hello.kiosk.domain.user.User

data class UserResponse(
    val username: String,
    val password: String
) {
    companion object {
        fun of(user: User): UserResponse {
            return UserResponse(
                username = user.username,
                password = user.password
            )
        }
    }
}

