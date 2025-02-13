package hello.kiosk.controller.order

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.order.request.OrderRequest
import hello.kiosk.service.order.OrderService
import hello.kiosk.service.order.request.OrderServiceRequest
import hello.kiosk.service.order.response.OrderResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/orders")
class OrderController (
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(@RequestBody request: OrderRequest,
                    requestHttp: HttpServletRequest
    ): ApiResponse<OrderResponse> {
        val username = findUsernameFrom(requestHttp)
        return ApiResponse.ok(orderService.createOrder(request.toServiceRequest(), LocalDateTime.now(), username))
    }


    private fun findUsernameFrom(requestHttp: HttpServletRequest) =
        requestHttp.getAttribute("username") as? String ?: throw IllegalArgumentException("인증된 사용자가 아닙니다.")
}