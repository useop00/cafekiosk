package hello.kiosk.controller.user

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.user.request.LoginRequest
import hello.kiosk.controller.user.request.SignRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.request.LoginServiceRequest
import hello.kiosk.service.user.request.SignServiceRequest
import hello.kiosk.service.user.response.LoginResponse
import hello.kiosk.service.user.response.UserResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class UserController (
    private val userService: UserService
) {
    @PostMapping("/sign")
    fun signUp(@RequestBody request: SignRequest): ApiResponse<UserResponse> {
        return ApiResponse.ok(userService.signUp(request.toServiceRequest()))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest):ApiResponse<LoginResponse>{
        return ApiResponse.ok(userService.login(request.toServiceRequest()))
    }
}