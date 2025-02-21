package hello.kiosk.controller.user

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.user.request.LoginRequest
import hello.kiosk.controller.user.request.SignRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.response.LoginResponse
import hello.kiosk.service.user.response.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "User", description = "사용자 API")
class UserController (
    private val userService: UserService
) {

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("/sign")
    fun signUp(@Valid @RequestBody request: SignRequest): ApiResponse<UserResponse> {
        return ApiResponse.ok(userService.signUp(request.toServiceRequest()))
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest):ApiResponse<LoginResponse>{
        return ApiResponse.ok(userService.login(request.toServiceRequest()))
    }
}