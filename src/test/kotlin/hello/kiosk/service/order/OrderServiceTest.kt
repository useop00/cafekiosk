package hello.kiosk.service.order

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.domain.stock.Stock
import hello.kiosk.exception.NotFoundProduct
import hello.kiosk.exception.OutOfStock
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.user.UserService
import hello.kiosk.service.user.request.LoginRequest
import hello.kiosk.service.user.request.SignRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrderServiceTest @Autowired constructor(
    private val orderService: OrderService,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository,
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
        val product1 = createProduct("A-001", COFFEE, 1000)
        val product2 = createProduct("A-002", COFFEE, 3000)
        val product3 = createProduct("A-003", COFFEE, 5000)
        productRepository.saveAll(listOf(product1, product2, product3))


        val orderRequest = OrderRequest(
            productNumber = listOf("A-001", "A-002"),
            productQuantities = mapOf(
                "A-001" to 2,
                "A-002" to 1
            )
        )

        //when
        val orderResponse = orderService.createOrder(orderRequest, LocalDateTime.now(), username)

        //then
        assertThat(orderResponse.totalPrice).isEqualTo(5000)
        assertThat(orderResponse.products).hasSize(2)
            .extracting("productNumber", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", 1000),
                tuple("A-002", 3000)
            )
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
        val dateTime = LocalDateTime.now()
        val product1 = createProduct("A-001", COFFEE, 2000)
        val product2 = createProduct("B-001", BOTTLE, 3000)
        val product3 = createProduct("C-001", DESSERT, 5000)
        productRepository.saveAll(listOf(product1, product2, product3))

        val stock1 = Stock.create("B-001", 5)
        val stock2 = Stock.create("C-001", 5)
        stockRepository.saveAll(listOf(stock1, stock2))


        val orderRequest = OrderRequest(
            productNumber = listOf("A-001", "B-001", "C-001"),
            productQuantities = mapOf(
                "A-001" to 1,
                "B-001" to 1,
                "C-001" to 1
            )
        )

        //when
        val response = orderService.createOrder(orderRequest, dateTime, username)

        //then
        assertThat(response.id).isNotNull()
        assertThat(response)
            .extracting("registeredDateTime", "totalPrice")
            .contains(dateTime, 10000)

        assertThat(response.products).hasSize(3)
            .extracting("productNumber", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", 2000),
                tuple("B-001", 3000),
                tuple("C-001", 5000)
            )

        assertThat(stock1.quantity).isEqualTo(4)
        assertThat(stock2.quantity).isEqualTo(4)
    }

    @Test
    fun `재고가 부족하면 예외가 발생된다`() {
        //given
        val product = createProduct("B-001", BOTTLE, 3000)
        productRepository.save(product)

        val orderRequest = OrderRequest(
            productNumber = listOf("B-001"),
            productQuantities = mapOf(
                "B-001" to 1
            )
        )

        // expect
        assertThrows<OutOfStock> {
            orderService.createOrder(orderRequest, LocalDateTime.now(), username)
        }.also {
            assertThat(it.message).isEqualTo("재고가 부족합니다.")
        }

    }

    private fun createProduct(productNumber: String, type:ProductType, price:Int) = Product.create(
        productNumber = productNumber,
        type = type,
        sellingStatus = SELLING,
        name = "메뉴이름",
        price = price
    )
}
