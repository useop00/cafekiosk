package hello.kiosk.service.user

import hello.kiosk.repository.UserRepository
import hello.kiosk.service.user.request.LoginServiceRequest
import hello.kiosk.service.user.request.SignServiceRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository
) {
    private val username = "wss3325"
    private val password = "1234"

    @Test
    fun sign() {
        //given
        val request = SignServiceRequest(username = username, password = password)

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
        val request = SignServiceRequest(username = username, password = password)
        userService.signUp(request)

        val loginRequest = LoginServiceRequest(username = username, password = password)

        //when
        val response = userService.login(loginRequest)

        //then
        assertThat(response.token).isNotNull()
    }

    @Test
    fun failedLogin() {
        //given
        val request = SignServiceRequest(username = username, password = password)
        userService.signUp(request)

        val loginRequest = LoginServiceRequest(username = username, password = "3333")

        //when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.login(loginRequest)
        }

        assertThat(exception.message).isEqualTo("비밀번호가 일치하지 않습니다.")

    }
}