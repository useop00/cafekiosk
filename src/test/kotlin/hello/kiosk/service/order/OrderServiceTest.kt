package hello.kiosk.service.order

import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.exception.NotFoundProduct
import hello.kiosk.exception.OutOfStock
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.request.ProductCreateRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.request.LoginRequest
import hello.kiosk.service.user.request.SignRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrderServiceTest @Autowired constructor(
    private val orderService: OrderService,
    private val productService: ProductService,
    private val stockRepository: StockRepository,
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
    fun `주문을 생성한다`() {
        //given
        val product1 = ProductCreateRequest(
            type = COFFEE,
            sellingStatus = SELLING,
            name = "아메리카노",
            price = 3000
        )
        val saveProduct1 = productService.saveProduct(product1)

        val product2 = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000,
            stock = 10
        )
        val saveProduct2 = productService.saveProduct(product2)


        val orderRequest = OrderRequest(
            productNumber = listOf(saveProduct1.productNumber, saveProduct2.productNumber),
            productQuantities = mapOf(
                saveProduct1.productNumber to 2,
                saveProduct2.productNumber to 3
            )
        )

        //when
        val orderResponse = orderService.createOrder(orderRequest, LocalDateTime.now(), username)

        //then
        assertThat(orderResponse.totalPrice).isEqualTo(21000)

        val stock = stockRepository.findByProductNumber(saveProduct2.productNumber)
        assertThat(stock!!.quantity).isEqualTo(7)
    }

    @Test
    fun `찾을 수 없는 상품이면 예외를 던진다`() {
        //given
        val orderRequest = OrderRequest(
            productNumber = listOf("A-001"),
            productQuantities = mapOf("A-001" to 1)
        )

        //expect
        assertThrows<NotFoundProduct> {
            orderService.createOrder(orderRequest, LocalDateTime.now(), username)
        }.also {
            assertThat(it.message).isEqualTo("상품을 찾을 수 없습니다.")
        }
    }

    @Test
    fun `BOTTLE, DESSERT 타입 상품은 주문 시 재고가 차감된다`() {
        //given
        val request = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000,
            stock = 10
        )
        val productResponse = productService.saveProduct(request)

        val orderRequest = OrderRequest(
            productNumber = listOf(productResponse.productNumber),
            productQuantities = mapOf(productResponse.productNumber to 5)
        )

        //when
        orderService.createOrder(orderRequest, LocalDateTime.now(), username)

        //then
        val stock = stockRepository.findByProductNumber(productResponse.productNumber)
        assertThat(stock!!.quantity).isEqualTo(5)

    }

    @Test
    fun `재고가 부족하면 예외가 발생된다`() {
        //given
        val request = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000,
            stock = 3
        )

        val productResponse = productService.saveProduct(request)

        val orderRequest = OrderRequest(
            productNumber = listOf(productResponse.productNumber),
            productQuantities = mapOf(productResponse.productNumber to 5)
        )

        // expect
        assertThrows<OutOfStock> {
            orderService.createOrder(orderRequest, LocalDateTime.now(), username)
        }.also {
            assertThat(it.message).isEqualTo("재고가 부족합니다.")
        }

    }

    @Test
    fun `동시에 같은 상품을 주문하면 낙관적 락으로 예외가 발생한다`() {
        //given
        val request = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000,
            stock = 5
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