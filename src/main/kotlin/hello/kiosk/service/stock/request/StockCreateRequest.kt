package hello.kiosk.service.stock.request

data class StockCreateRequest(
    val productNumber: String,
    val quantity: Int
)
