package hello.kiosk.service.stock.response

import hello.kiosk.domain.stock.Stock

data class StockResponse(
    val id: Long,
    val productNumber: String,
    val quantity: Int
) {
    companion object {
        fun of(stock:Stock): StockResponse {
            return StockResponse(
                id = stock.id!!,
                productNumber = stock.productNumber,
                quantity = stock.quantity
            )
        }
    }
}
