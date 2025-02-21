package hello.kiosk.service.stock

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductType.BOTTLE
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.order.OrderService
import hello.kiosk.service.order.request.OrderServiceRequest
import hello.kiosk.service.stock.request.StockCreateServiceRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.request.LoginServiceRequest
import hello.kiosk.service.user.request.SignServiceRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
class PessimisticLockTest @Autowired constructor(
    private val orderService: OrderService,
    private val stockRepository: StockRepository,
    private val stockService: StockService,
    private val userService: UserService,
    private val productRepository: ProductRepository
) {
    lateinit var username: String
    lateinit var token: String

    @BeforeEach
    fun setUp() {
        val signUpRequest = SignServiceRequest(username = "wss3325", password = "1234")
        userService.signUp(signUpRequest)

        val loginRequest = LoginServiceRequest(username = signUpRequest.username, password = signUpRequest.password)
        val loginResponse = userService.login(loginRequest)

        username = signUpRequest.username
        token = loginResponse.token
    }

    @DisplayName("동시에 주문을 생성하면 재시도를 통해 주문이 생성된다.")
    @Test
    fun concurrency() {
        val product = Product.create(
            productNumber = "B-001",
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "탄산수",
            price = 2000
        )
        productRepository.save(product)

        stockService.upsertStock(StockCreateServiceRequest("B-001", 100))

        val thread = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(thread)

        for (i in 0 until thread) {
            executorService.submit {
                try {
                    val request = OrderServiceRequest(
                        productNumber = listOf("B-001"),
                        productQuantities = mapOf("B-001" to 1)
                    )
                    val registeredDateTime = LocalDateTime.now()
                    val username = this.username

                    orderService.createOrder(request, registeredDateTime, username)
                } catch (e: Exception) {
                    println("Thread ${Thread.currentThread().name} - 주문 실패: ${e.message}")
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()

        val result = stockRepository.findByProductNumber("B-001")
        assertThat(result?.quantity).isZero()
    }
}