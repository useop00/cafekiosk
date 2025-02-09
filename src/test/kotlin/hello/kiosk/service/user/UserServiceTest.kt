package hello.kiosk.service.user

import hello.kiosk.config.PasswordUtil
import hello.kiosk.repository.UserRepository
import hello.kiosk.service.user.request.LoginRequest
import hello.kiosk.service.user.request.SignRequest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.math.log
import kotlin.test.Test

@SpringBootTest
@Transactional
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository
) {
    private val username = "wss3325"
    private val password = "1234"

    @Test
    fun sign() {
        //given
        val request = SignRequest(username = username, password = password)

        //when
        val response = userService.signUp(request)

        //then
        assertThat(response.username).isEqualTo(username)
        val savedUser = userRepository.findByUsername(username)
        assertThat(savedUser).isNotNull
        assertThat(savedUser!!.username).isEqualTo(username)
    }

    @Test
    fun successLogin() {
        //given
        val request = SignRequest(username = username, password = password)
        userService.signUp(request)

        val loginRequest = LoginRequest(username = username, password = password)

        //when
        val response = userService.login(loginRequest)

        //then
        assertThat(response.token).isNotNull()
    }

    @Test
    fun failedLogin() {
        //given
        val request = SignRequest(username = username, password = password)
        userService.signUp(request)

        val loginRequest = LoginRequest(username = username, password = "3333")

        //when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.login(loginRequest)
        }

        assertThat(exception.message).isEqualTo("비밀번호가 일치하지 않습니다.")

    }
}