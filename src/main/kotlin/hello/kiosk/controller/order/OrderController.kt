package hello.kiosk.controller.order

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.order.request.OrderRequest
import hello.kiosk.service.order.OrderService
import hello.kiosk.service.order.response.OrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "주문 API")
class OrderController (
    private val orderService: OrderService
) {

    @Operation(summary = "주문 생성", description = "로그인 한 사용자가 주문을 생성합니다.")
    @PostMapping
    fun createOrder(@Valid @RequestBody request: OrderRequest,
                    requestHttp: HttpServletRequest
    ): ApiResponse<OrderResponse> {
        val username = findUsernameFrom(requestHttp)
        return ApiResponse.ok(orderService.createOrder(request.toServiceRequest(), LocalDateTime.now(), username))
    }


    private fun findUsernameFrom(requestHttp: HttpServletRequest) =
        requestHttp.getAttribute("username") as? String ?: throw IllegalArgumentException("인증된 사용자가 아닙니다.")
}