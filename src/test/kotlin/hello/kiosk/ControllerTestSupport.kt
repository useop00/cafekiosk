package hello.kiosk

import com.fasterxml.jackson.databind.ObjectMapper
import hello.kiosk.config.JwtInterceptor
import hello.kiosk.config.JwtProvider
import hello.kiosk.controller.order.OrderController
import hello.kiosk.controller.product.ProductController
import hello.kiosk.controller.stock.StockController
import hello.kiosk.controller.user.UserController
import hello.kiosk.service.order.OrderService
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.stock.StockService
import hello.kiosk.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    controllers = [
        OrderController::class,
        ProductController::class,
        StockController::class,
        UserController::class
    ]
)
abstract class ControllerTestSupport {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockitoBean
    protected lateinit var orderService: OrderService

    @MockitoBean
    protected lateinit var productService: ProductService

    @MockitoBean
    protected lateinit var stockService: StockService

    @MockitoBean
    protected lateinit var jwtProvider: JwtProvider

    @MockitoBean
    protected lateinit var userService: UserService

    @MockitoBean
    protected lateinit var jwtInterceptor: JwtInterceptor
}