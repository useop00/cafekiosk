package hello.kiosk.repository

import hello.kiosk.IntegrationTestSupport
import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductSellingStatus.SOLD_OUT
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class ProductRepositoryTest @Autowired constructor(
    private var productRepository: ProductRepository

) : IntegrationTestSupport() {

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    fun findAllBySellingStatusIn() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("B-001", BOTTLE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("C-001", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productRepository.findAllBySellingStatusIn(listOf(SELLING))

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", "아메리카노", 3000),
                tuple("B-001", "카페라떼", 5000)
            )

    }

    @DisplayName("상품번호 리스트에 해당하는 상품들을 조회한다.")
    @Test
    fun findAllByProductNumberIn() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("A-002", BOTTLE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("A-003", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        val product4 = createProduct("B-001", BOTTLE, SOLD_OUT, "핫초코", 5000)
        productRepository.saveAll(listOf(product1, product2, product3, product4))

        //when
        val products = productRepository.findAllByProductNumberIn(listOf("A-001", "A-002"))

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "name", "sellingStatus")
            .containsExactlyInAnyOrder(
                tuple("A-001", "아메리카노", SELLING),
                tuple("A-002", "카페라떼", SELLING)
            )

    }

    @DisplayName("마지막 상품 번호의 상품을 조회한다.")
    @Test
    fun findLatestProduct() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "라떼", 3000)
        val product2 = createProduct("A-002", COFFEE, SELLING, "더치커피", 3000)
        val product3 = createProduct("A-003", COFFEE, SELLING, "아메리카노", 3000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val latest = productRepository.findLatestProduct("A")

        //then
        assertThat(latest).isEqualTo("A-003")
    }

    @DisplayName("상품번호의 prefix로 상품들을 조회한다.")
    @Test
    fun findByProductNumberByPrefix() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("A-002", COFFEE, SELLING, "더치커피", 3000)
        val product3 = createProduct("B-001", COFFEE, SELLING, "핫초코", 3000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productRepository.findByProductNumberByPrefix("A")

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001", "아메리카노", 3000),
                tuple("A-002", "더치커피", 3000)
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