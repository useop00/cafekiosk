package hello.kiosk.repository

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductSellingStatus.SOLD_OUT
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class ProductRepositoryTest @Autowired constructor(
    private var productRepository: ProductRepository
) {

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    fun findAllBySellingStatusIn() {
        //given
        val product1 = createProduct("001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("002", BEVERAGE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("003", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productRepository.findAllBySellingStatusIn(listOf(SELLING))

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001", "아메리카노", 3000),
                tuple("002", "카페라떼", 5000)
            )

    }

    @DisplayName("상품번호 리스트에 해당하는 상품들을 조회한다.")
    @Test
    fun findAllByProductNumberIn() {
        //given
        val product1 = createProduct("001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("002", BEVERAGE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("003", DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productRepository.findAllByProductNumberIn(listOf("001", "002"))

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber", "name", "sellingStatus")
            .containsExactlyInAnyOrder(
                tuple("001", "아메리카노", SELLING),
                tuple("002", "카페라떼", SELLING)
            )

    }

    @DisplayName("마지막으로 저장한 상품번호를 조회한다.")
    @Test
    fun findLatestProduct() {
        //given
        val targetProductNumber = "003"
        val product1 = createProduct("001", COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("002", BEVERAGE, SELLING, "카페라떼", 5000)
        val product3 = createProduct(targetProductNumber, DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val latestProduct = productRepository.findLatestProduct()

        //then
        assertThat(latestProduct).isEqualTo("003")
    }

    @DisplayName("상품이 하나도 없을 때 null을 반환한다.")
    @Test
    fun findLatestProductIsEmpty() {
        //when
        val latestProduct = productRepository.findLatestProduct()

        //then
        assertThat(latestProduct).isNull()
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