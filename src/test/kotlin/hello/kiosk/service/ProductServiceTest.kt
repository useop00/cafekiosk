package hello.kiosk.service

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductSellingStatus.SELLING
import hello.kiosk.domain.product.ProductSellingStatus.SOLD_OUT
import hello.kiosk.domain.product.ProductType
import hello.kiosk.domain.product.ProductType.*
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
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
    private var productService: ProductService,
    private var stockRepository: StockRepository
) {

    @DisplayName("상품을 저장한다.")
    @Test
    fun createProduct() {
        //given
        val product1 = createProduct("A-001", COFFEE, SELLING, "아메리카노", 3000)
        productRepository.save(product1)

        val request = ProductCreateRequest(
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

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    fun findSellingProducts() {
        //given
        val product1 = createProduct("A-001",COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("B-001",BOTTLE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("C-001",DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val sellingProducts = productService.getSellingProducts()

        //then
        assertThat(sellingProducts).hasSize(2)
            .extracting("productNumber","type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001",COFFEE, SELLING, "아메리카노", 3000),
                tuple("002",BOTTLE, SELLING, "카페라떼", 5000)
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
        val request = ProductCreateRequest(
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
        val request = ProductCreateRequest(
            type = COFFEE,
            sellingStatus = SELLING,
            name = "콜드브루",
            price = 7000
        )

        val response = productService.saveProduct(request)

        //then
        assertThat(response.productNumber).isEqualTo("A-002")

    }

    @DisplayName("원하는 타입의 상품들을 조회한다.")
    @Test
    fun findPrefixProducts() {
        //given
        val product1 = createProduct("A-001",COFFEE, SELLING, "아메리카노", 3000)
        val product2 = createProduct("A-002",BOTTLE, SELLING, "카페라떼", 5000)
        val product3 = createProduct("C-001",DESSERT, SOLD_OUT, "치즈케이크", 7000)
        productRepository.saveAll(listOf(product1, product2, product3))

        //when
        val products = productService.getSellingProductsType("A")

        //then
        assertThat(products).hasSize(2)
            .extracting("productNumber","type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("A-001",COFFEE, SELLING, "아메리카노", 3000),
                tuple("A-002",BOTTLE, SELLING, "카페라떼", 5000)
            )
    }

    @Test
    fun `BOTTLE 타입 상품 생성 시 재고도 함께 생성된다`() {
        //given
        val request = ProductCreateRequest(
            type = BOTTLE,
            sellingStatus = SELLING,
            name = "콜드브루 병",
            price = 5000,
            initialStock = 10
        )

        //when
        val response = productService.saveProduct(request)

        //then
        val savedProduct = productRepository.findByProductNumberByPrefix(response.productNumber)
        assertThat(savedProduct).isNotNull

        val stock = stockRepository.findByProductNumber(response.productNumber)
        assertThat(stock!!.quantity).isEqualTo(10)
    }

    @Test
    fun `COFFEE 타입 상품은 재고가 생성되지 않는다`() {
        //given
        val request = ProductCreateRequest(
            type = COFFEE,
            sellingStatus = SELLING,
            name = "아메리카노",
            price = 5000,
            initialStock = 10
        )

        //when
        val response = productService.saveProduct(request)

        //then
        val stock = stockRepository.findByProductNumber(response.productNumber)
        assertThat(stock).isNull()
    }

    private fun createProduct(
        productNumber: String,
        type: ProductType,
        sellingStatus: ProductSellingStatus,
        name: String,
        price: Int,
        initialStock: Int? = null
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