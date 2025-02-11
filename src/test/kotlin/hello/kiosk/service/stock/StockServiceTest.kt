package hello.kiosk.service.stock

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus.*
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.domain.stock.Stock
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.stock.request.StockCreateRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class StockServiceTest @Autowired constructor(
    private val stockService: StockService,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository
) {

    @DisplayName("재고가 없으면 새로 생성한다.")
    @Test
    fun createStock() {
        //given
        val product = createProduct()
        productRepository.save(product)

        val request = StockCreateRequest(
            productNumber = product.productNumber,
            quantity = 10
        )

        //when
        val response = stockService.upsertStock(request)

        //then
        assertThat(response).extracting("productNumber", "quantity")
            .containsExactly(product.productNumber, 10)
    }

    @DisplayName("이미 재고가 있으면 수량을 증가한다.")
    @Test
    fun updateStock() {
        //given
        val product = createProduct()
        productRepository.save(product)

        val stock = Stock.create(product.productNumber, 5)
        stockRepository.save(stock)

        val request = StockCreateRequest(
            productNumber = product.productNumber,
            quantity = 10
        )

        //when
        val response = stockService.upsertStock(request)

        //then
        assertThat(response).extracting("productNumber", "quantity")
            .containsExactly(product.productNumber, 15)
    }

    @Test
    fun getAllStocks() {
        //given
        val product1 = createProduct()
        val product2 = Product.create(
            productNumber = "B-002",
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "사이다",
            price = 2000
        )
        productRepository.saveAll(listOf(product1, product2))

        val request1 = StockCreateRequest(
            productNumber = product1.productNumber,
            quantity = 10
        )
        val request2 = StockCreateRequest(
            productNumber = product2.productNumber,
            quantity = 5
        )

        stockService.upsertStock(request1)
        stockService.upsertStock(request2)

        //when
        val response = stockService.getAllStocks()

        //then
        assertThat(response).hasSize(2)
            .extracting("productNumber", "quantity")
            .containsExactlyInAnyOrder(
                tuple(product1.productNumber, 10),
                tuple(product2.productNumber, 5)
            )

    }

    private fun createProduct() = Product.create(
        productNumber = "B-001",
        type = BOTTLE,
        sellingStatus = SELLING,
        name = "콜라",
        price = 3000
    )


}