package hello.kiosk.domain.orderProduct

import com.fasterxml.jackson.annotation.JsonIgnore
import hello.kiosk.domain.BaseEntity
import hello.kiosk.domain.order.Order
import hello.kiosk.domain.product.Product
import jakarta.persistence.*

@Entity
class OrderProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    var product: Product,

    var quantity: Int

):BaseEntity(){
    companion object{
        fun create(order: Order, product: Product, quantity: Int = 1): OrderProduct {
            return OrderProduct(
                order = order,
                product = product,
                quantity = quantity
            )
        }
    }
}