package hello.kiosk.service.order

import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductType.BOTTLE
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.request.ProductCreateRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.request.LoginRequest
import hello.kiosk.service.user.request.SignRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.Callable


@SpringBootTest
@ActiveProfiles("test")
class StockConcurrencyTest @Autowired constructor(
    private val orderService: OrderService,
    private val productService: ProductService,
    private val userService: UserService
) {
    lateinit var username: String
    lateinit var token: String

    @BeforeEach
    fun setUp() {
        val signUpRequest = SignRequest(username = "wss3325", password = "1234")
        userService.signUp(signUpRequest)

        val loginRequest = LoginRequest(username = signUpRequest.username, password = signUpRequest.password)
        val loginResponse = userService.login(loginRequest)

        username = signUpRequest.username
        token = loginResponse.token
    }

    @Test
    fun `동시에 같은 상품을 주문하면 낙관적 락으로 예외가 발생한다`() {
        //given
        val request = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000
        )
        val product = productService.saveProduct(request)

        //when
        val order = OrderRequest(
            productNumber = listOf(product.productNumber),
            productQuantities = mapOf(product.productNumber to 4)
        )

        val executor = Executors.newFixedThreadPool(2)
        val futures = (1..2).map {
            executor.submit(Callable {
                try {
                    Thread.sleep(100)
                    orderService.createOrder(order, LocalDateTime.now(), username)
                    "success-$it"
                } catch (e: Exception) {
                    "fail-$it: ${e.message}"
                }
            })
        }

        //then
        val results = futures.map { it.get() }
        println(results)

        /**
         * 하나는 성공 (재고 5 -> 1), 다른 하나는 낙관적 락에 의해 실패
         */
        assertThat(results.count { it.startsWith("success") }).isOne()
        assertThat(results.count { it.startsWith("fail") }).isOne()
    }
}