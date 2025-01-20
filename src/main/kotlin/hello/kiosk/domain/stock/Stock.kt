package hello.kiosk.domain.stock

import hello.kiosk.domain.BaseEntity
import hello.kiosk.exception.OutOfStock
import jakarta.persistence.*

@Entity
class Stock (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var productNumber: String,
    var quantity: Int = 0,

    @Version
    var version: Long? = null

): BaseEntity(){
    companion object {
        fun create(productNumber: String, quantity: Int): Stock {
            return Stock(
                productNumber = productNumber,
                quantity = quantity
            )
        }
    }

    fun isQuantityLessThan(quantity: Int): Boolean {
        return this.quantity < quantity
    }

    fun decreaseQuantity(quantity: Int) {
        if (isQuantityLessThan(quantity)) {
            throw OutOfStock("재고가 부족합니다.")
        }
        this.quantity -= quantity
    }
}