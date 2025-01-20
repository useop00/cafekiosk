package hello.kiosk.service.order

import hello.kiosk.domain.order.Order
import hello.kiosk.domain.product.Product
import hello.kiosk.exception.NotFoundProduct
import hello.kiosk.exception.OutOfStock
import hello.kiosk.repository.OrderRepository
import hello.kiosk.repository.ProductRepository
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.order.request.OrderRequest
import hello.kiosk.service.order.response.OrderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository
) {

    @Transactional
    fun crateOrder(request: OrderRequest, registeredDateTime: LocalDateTime): OrderResponse {
        val productNumber = request.productNumber
        val products = findProductsBy(productNumber)

        reduceStockBy(products, request)

        val order = Order.create(products, request.productQuantities ,registeredDateTime)
        val savedOrder = orderRepository.save(order)

        return OrderResponse.of(savedOrder)
    }

    private fun findProductsBy(productNumbers: List<String>): List<Product> {
        val products = productRepository.findAllByProductNumberIn(productNumbers)
        val productMap = products.associateBy { it.productNumber }

        return productNumbers.map {
            productMap[it] ?: throw NotFoundProduct("상품을 찾을 수 없습니다.")
        }
    }

    private fun reduceStockBy(
        products: List<Product>,
        request: OrderRequest
    ) {
        for (product in products) {
            if (product.type.needStockCheck()) {
                val requestQuantity = request.productQuantities[product.productNumber] ?: 1
                decreaseStock(product.productNumber, requestQuantity)
            }
        }
    }

    private fun decreaseStock(productNumber: String, requestQuantity: Int) {
        val stock = stockRepository.findByProductNumber(productNumber)
            ?: throw OutOfStock("해당 상품의 재고가 없습니다.")

        stock.decreaseQuantity(requestQuantity)
        stockRepository.save(stock)
    }
}