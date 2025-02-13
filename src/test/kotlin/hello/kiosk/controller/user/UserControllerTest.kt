package hello.kiosk.controller.user

import hello.kiosk.ControllerTestSupport
import hello.kiosk.controller.user.request.SignRequest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post
import kotlin.test.Test

class UserControllerTest : ControllerTestSupport() {

    @Test
    fun signup() {
        //given
        val request = SignRequest(
            username = "wss3325",
            password = "1234"
        )

        //when //then
        mockMvc.post("/auth/sign") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }
            .andDo {
                print()
            }
    }

    @Test
    fun login() {
        //given
        val request = SignRequest(
            username = "wss3325",
            password = "1234"
        )

        //when //then
        mockMvc.post("/auth/login") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }
            .andDo {
                print()
            }

    }
}