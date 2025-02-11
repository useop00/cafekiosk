package hello.kiosk.service.order

import hello.kiosk.domain.order.Order
import hello.kiosk.domain.product.Product
import hello.kiosk.domain.stock.Stock
import hello.kiosk.exception.NotFoundProduct
import hello.kiosk.exception.NotFoundUser
import hello.kiosk.exception.OutOfStock
import hello.kiosk.repository.OrderRepository
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
import hello.kiosk.repository.UserRepository
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.order.response.OrderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createOrder(request: OrderRequest, registeredDateTime: LocalDateTime, username: String): OrderResponse {
        val productNumber = request.productNumber
        val products = findProductsBy(productNumber)

        val user = userRepository.findByUsername(username) ?: throw NotFoundUser("사용자를 찾을 수 없습니다.")

        deductStockQuantity(products, request.productQuantities)

        val order = Order.create(products, request.productQuantities, registeredDateTime, user)
        val savedOrder = orderRepository.save(order)

        return OrderResponse.of(savedOrder)
    }

    private fun findProductsBy(productNumbers: List<String>): List<Product> {
        val products = productRepository.findAllByProductNumberIn(productNumbers)
        val productMap = products.associateBy { it.productNumber }

        return productNumbers.map { productNumber ->
            productMap[productNumber] ?: throw NotFoundProduct("상품을 찾을 수 없습니다.")
        }
    }

    private fun deductStockQuantity(products: List<Product>, productQuantities: Map<String, Int>) {
        val stockProductNumbers = extractStockProductNumbers(products)

        val stockMap = createStockMapBy(stockProductNumbers)

        val productCountingMap = createCountingMap(stockProductNumbers, productQuantities)

        for (stockProductNumber in stockProductNumbers.toSet()) {
            val stock = stockMap[stockProductNumber] ?: throw OutOfStock("재고가 부족합니다.")
            val quantity = productCountingMap[stockProductNumber]?.toInt() ?: 0

            if (stock.isQuantityLessThan(quantity)) {
                throw OutOfStock("재고가 부족합니다. 상품번호: $stockProductNumber")
            }
            stock.decreaseQuantity(quantity)
        }
    }

    private fun extractStockProductNumbers(products: List<Product>): List<String> {
        return products
            .filter { it.type.needStockCheck() }
            .map { it.productNumber }
    }

    private fun createStockMapBy(stockProductNumbers: List<String>): Map<String, Stock> {
        val stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers)
        return stocks.associateBy { it.productNumber }
    }

    private fun createCountingMap(
        stockProductNumbers: List<String>,
        productQuantities: Map<String, Int>
    ): Map<String, Long> {
        val countingMap = mutableMapOf<String, Long>()

        stockProductNumbers.forEach { productNumber ->
            val quantity = productQuantities[productNumber]?.toLong() ?: 1
            countingMap[productNumber] = (countingMap[productNumber] ?: 0L) + quantity
        }

        return countingMap
    }
}
