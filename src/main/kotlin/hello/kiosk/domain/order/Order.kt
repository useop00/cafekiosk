package hello.kiosk.domain.order

import hello.kiosk.domain.BaseEntity
import hello.kiosk.domain.orderProduct.OrderProduct
import hello.kiosk.domain.product.Product
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.INIT,

    var totalPrice: Int = 0,

    var registeredDateTime: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var orderProducts: MutableList<OrderProduct> = mutableListOf()


) : BaseEntity() {

    companion object {
        fun create(products: List<Product>,
                   productQuantity: Map<String, Int>,
                   registeredDateTime: LocalDateTime
        ): Order {
            val order = Order(
                orderStatus = OrderStatus.INIT,
                registeredDateTime = registeredDateTime
            )
            order.totalPrice = calculateTotalPrice(products, productQuantity)
            order.orderProducts = products.map { product ->
                val quantity = productQuantity[product.productNumber]?:1
                OrderProduct(order = order, product = product, quantity = quantity)
            }.toMutableList()

            return order
        }

        private fun calculateTotalPrice(products: List<Product>,
                                        productQuantity: Map<String, Int>
        ): Int {
            return products.sumOf { product ->
                val quantity = productQuantity[product.productNumber]?:1
                product.price * quantity
            }
        }
    }

    fun changeOrderStatus(orderStatus: OrderStatus) {
        this.orderStatus = orderStatus
    }
}