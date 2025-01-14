package hello.kiosk.service

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductSellingStatus.SOLD_OUT
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.repository.ProductRepository
import hello.kiosk.service.product.request.ProductCreateRequest
import hello.kiosk.service.product.ProductService
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class ProductServiceTest @Autowired constructor(
    private var productRepository: ProductRepository,
    private var productService: ProductService
) {

    @DisplayName("상품을 저장한다.")
    @Test
    fun createProduct() {
        //given
        val product1 = createProduct("001", COFFEE, SELLING, "아메리카노", 3000)
        productRepository.save(product1)

        val request = ProductCreateRequest(
            type = BEVERAGE,
            sellingStatus = SELLING,
            name = "카페라떼",
            price = 5000
        )

        //when
        val response = productService.saveProduct(request)

        //then
        assertThat(response)
            .extracting("type", "sellingStatus", "name", "price")
            .contains(BEVERAGE, SELLING, "카페라떼", 5000)

        val products = productRepository.findAll();
        assertThat(products).hasSize(2)
            .extracting("type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple(COFFEE, SELLING, "아메리카노", 3000),
                tuple(BEVERAGE, SELLING, "카페라떼", 5000)
            )
    }

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    fun findSellingProducts() {
        //given
        val product1 = createProduct("001",COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("002",BEVERAGE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("003",DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val sellingProducts = productService.getSellingProducts()

        //then
        assertThat(sellingProducts).hasSize(2)
            .extracting("productNumber","type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001",COFFEE, SELLING, "아메리카노", 3000),
                tuple("002",BEVERAGE, SELLING, "카페라떼", 5000)
            )
    }

    @DisplayName("다음 상품번호를 생성한다.")
    @Test
    fun nextProductNumber() {
        //given
        val product1 = createProduct("001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("002", BEVERAGE, SELLING, "카페라떼", 5000)
        productRepository.saveAll(listOf(product1, product2))

        //when
        val request = ProductCreateRequest(
            type = DESSERT,
            sellingStatus = SELLING,
            name = "치즈케이크",
            price = 7000
        )

        val response = productService.saveProduct(request)

        //then
        assertThat(response.productNumber).isEqualTo("003")

    }

    private fun createProduct(
        productNumber: String,
        type: ProductType,
        sellingStatus: ProductSellingStatus,
        name: String,
        price: Int
    ): Product {
        return Product.create(
            productNumber = productNumber,
            type = type,
            sellingStatus = sellingStatus,
            name = name,
            price = price
        )
    }

}