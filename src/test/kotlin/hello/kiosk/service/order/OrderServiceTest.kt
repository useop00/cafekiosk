package hello.kiosk.service.order

import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.request.ProductCreateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@SpringBootTest
@Transactional
class OrderServiceTest @Autowired constructor(
 private val orderService: OrderService,
 private val productService: ProductService,
 private val stockRepository: StockRepository
) {

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
   initialStock = 10
  )
  val saveProduct2 = productService.saveProduct(product2)


  val orderRequest = OrderRequest(
        productNumber = listOf(saveProduct1.productNumber, saveProduct2.productNumber),
        productQuantities = mapOf(
         saveProduct1.productNumber to 2,
         saveProduct2.productNumber to 3)
    )

  //when
  val orderResponse = orderService.crateOrder(orderRequest, LocalDateTime.now())

  //then
  assertThat(orderResponse.totalPrice).isEqualTo(21000)

  val stock = stockRepository.findByProductNumber(saveProduct2.productNumber)
  assertThat(stock!!.quantity).isEqualTo(7)
 }

 @Test
 fun `BOTTLE, DESSERT 타입 상품은 주문 시 재고가 차감된다`() {
  //given
  val request = ProductCreateRequest(
   type = BOTTLE,
   sellingStatus = SELLING,
   name = "콜드브루 병",
   price = 5000,
   initialStock = 10
  )
  val productResponse = productService.saveProduct(request)

  val orderRequest = OrderRequest(
   productNumber = listOf(productResponse.productNumber),
   productQuantities = mapOf(productResponse.productNumber to 5)
  )

  //when
  orderService.crateOrder(orderRequest, LocalDateTime.now())

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
   initialStock = 3
  )

  val productResponse = productService.saveProduct(request)

  val orderRequest = OrderRequest(
   productNumber = listOf(productResponse.productNumber),
   productQuantities = mapOf(productResponse.productNumber to 5)
  )

  // expect
  assertThrows<IllegalArgumentException> {
   orderService.crateOrder(orderRequest, LocalDateTime.now())
  }.also {
   assertThat(it.message).contains("재고가 부족합니다.")
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
   initialStock = 5
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
     orderService.crateOrder(order, LocalDateTime.now())
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