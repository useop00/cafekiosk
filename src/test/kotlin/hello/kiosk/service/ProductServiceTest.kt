package hello.kiosk.service

import hello.kiosk.IntegrationTestSupport
import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductSellingStatus.SOLD_OUT
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.repository.ProductRepository
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.request.ProductCreateServiceRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProductServiceTest @Autowired constructor(
    private var productRepository: ProductRepository,
    private var productService: ProductService

) : IntegrationTestSupport() {

    @DisplayName("상품을 저장한다.")
    @Test
    fun createProduct() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        productRepository.save(product1)

        val request = ProductCreateServiceRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "카페라떼",
            price = 5000
        )

        //when
        val response = productService.saveProduct(request)

        //then
        assertThat(response)
            .extracting("type", "sellingStatus", "name", "price")
            .contains(BOTTLE, SELLING, "카페라떼", 5000)

        val products = productRepository.findAll();
        assertThat(products).hasSize(2)
            .extracting("type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple(COFFEE, SELLING, "아메리카노", 3000),
                tuple(BOTTLE, SELLING, "카페라떼", 5000)
            )
    }

    @DisplayName("판매중인 상품들을 조회한다.")
    @Test
    fun findSellingProducts() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("B-001", BOTTLE, SELLING, "콜라", 5000)
        val product3 = createProduct("C-001", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val sellingProducts = productService.getSellingProducts()

        //then
        assertThat(sellingProducts).hasSize(2)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", COFFEE, SELLING, "아메리카노", 3000),
                tuple("B-001", BOTTLE, SELLING, "콜라", 5000)
            )
    }

    @DisplayName("타입에 따라 prefix를 생성한다.")
    @Test
    fun nextProductType() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("B-001", BOTTLE, SELLING, "카페라떼", 5000)
        productRepository.saveAll(listOf(product1, product2))

        //when
        val request = ProductCreateServiceRequest(
            type = DESSERT,
            sellingStatus = SELLING,
            name = "치즈케이크",
            price = 7000
        )

        val response = productService.saveProduct(request)

        //then
        assertThat(response.productNumber).isEqualTo("C-001")

    }

    @DisplayName("다음 상품번호를 생성한다.")
    @Test
    fun nextProductNumber() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("B-001", BOTTLE, SELLING, "카페라떼", 5000)
        productRepository.saveAll(listOf(product1, product2))

        //when
        val request = ProductCreateServiceRequest(
            type = COFFEE,
            sellingStatus = SELLING,
            name = "콜드브루",
            price = 7000
        )

        val response = productService.saveProduct(request)

        //then
        assertThat(response.productNumber).isEqualTo("A-002")
    }

    @DisplayName("첫번째 상품번호를 생성한다.")
    @Test
    fun firstProductNumber() {
        //given
        val request1 = ProductCreateServiceRequest(
            type = COFFEE,
            sellingStatus = SELLING,
            name = "아메리카노",
            price = 3000
        )

        val request2 = ProductCreateServiceRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜라",
            price = 3000
        )

        //when
        val response1 = productService.saveProduct(request1)
        val response2 = productService.saveProduct(request2)

        //then
        val products = productRepository.findAll()
        assertThat(products).hasSize(2)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", COFFEE, SELLING, "아메리카노", 3000),
                tuple("B-001", BOTTLE, SELLING, "콜라", 3000)
            )

    }

    @DisplayName("원하는 타입의 상품들을 조회한다.")
    @Test
    fun findPrefixProducts() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("A-002", BOTTLE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("C-001", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productService.getSellingProductsType("A")

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", COFFEE, SELLING, "아메리카노", 3000),
                tuple("A-002", BOTTLE, SELLING, "카페라떼", 5000)
            )
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