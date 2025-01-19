package hello.kiosk.service.order.response

import hello.kiosk.domain.order.Order
import hello.kiosk.domain.order.OrderStatus
import hello.kiosk.domain.orderProduct.OrderProduct
import java.time.LocalDateTime

data class OrderResponse(

    val id: Long,
    var orderStatus: OrderStatus = OrderStatus.INIT,
    var totalPrice: Int,
    var registeredDateTime: LocalDateTime,
    var orderProducts: MutableList<OrderProduct>
) {
    companion object {
        fun of(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id!!,
                orderStatus = order.orderStatus,
                totalPrice = order.totalPrice,
                registeredDateTime = order.registeredDateTime,
                orderProducts = order.orderProducts
            )
        }
    }

}