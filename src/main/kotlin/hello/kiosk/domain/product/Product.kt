package hello.kiosk.domain.product

import hello.kiosk.domain.BaseEntity
import jakarta.persistence.*

@Entity
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var productNumber: String,

    @Enumerated(EnumType.STRING)
    var type: ProductType,

    @Enumerated(EnumType.STRING)
    var sellingStatus: ProductSellingStatus,

    var name: String,
    var price: Int

) : BaseEntity(){

    companion object {
        fun create(
            productNumber: String,
            type: ProductType,
            sellingStatus: ProductSellingStatus,
            name: String,
            price: Int
        ): Product {
            return Product(
                productNumber = productNumber,
                type = type,
                sellingStatus = sellingStatus,
                name = name,
                price = price
            )
        }
    }
}


