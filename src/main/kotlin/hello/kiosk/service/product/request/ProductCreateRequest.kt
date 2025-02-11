package hello.kiosk.service.product.request

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductType

data class ProductCreateRequest(

    val type: ProductType,
    val sellingStatus: ProductSellingStatus,
    val name: String,
    val price: Int
) {
    fun toEntity(nextProductNumber: String): Product {
        return Product.create(
            productNumber = nextProductNumber,
            type = type,
            sellingStatus = sellingStatus,
            name = name,
            price = price
        )
    }
}
