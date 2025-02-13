package hello.kiosk.service.user

import hello.kiosk.config.JwtProvider
import hello.kiosk.config.PasswordUtil
import hello.kiosk.domain.user.User
import hello.kiosk.repository.UserRepository
import hello.kiosk.service.user.request.LoginServiceRequest
import hello.kiosk.service.user.request.SignServiceRequest
import hello.kiosk.service.user.response.LoginResponse
import hello.kiosk.service.user.response.UserResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider
) {

    fun signUp(request: SignServiceRequest): UserResponse {
        val salt = PasswordUtil.generateSalt()
        val hashPassword = PasswordUtil.hashPassword(request.password, salt)

        val user = request.toEntity(hashPassword, salt)
        userRepository.save(user)
        return UserResponse.of(user)
    }

    fun login(request: LoginServiceRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")


        if (isNotEqualsPassword(request, user)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val token = jwtProvider.generateToken(user.username)
        return LoginResponse(token)
    }

    private fun isNotEqualsPassword(
        request: LoginServiceRequest,
        user: User
    ) = !PasswordUtil.verifyPassword(request.password, user.salt, user.password)

}