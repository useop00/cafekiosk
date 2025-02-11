package hello.kiosk.service.order.response

import hello.kiosk.domain.order.Order
import hello.kiosk.domain.order.OrderStatus
import hello.kiosk.service.product.response.ProductResponse
import java.time.LocalDateTime

data class OrderResponse(

    val id: Long,
    var orderStatus: OrderStatus = OrderStatus.INIT,
    var totalPrice: Int,
    var registeredDateTime: LocalDateTime,
    var products: List<ProductResponse>
) {
    companion object {
        fun of(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id!!,
                orderStatus = order.orderStatus,
                totalPrice = order.totalPrice,
                registeredDateTime = order.registeredDateTime,
                products = order.orderProducts.map { orderProduct ->
                    ProductResponse.of(orderProduct.product)
                }
            )
        }
    }

}