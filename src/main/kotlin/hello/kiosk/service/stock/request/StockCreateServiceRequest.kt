package hello.kiosk.service.stock.request

data class StockCreateServiceRequest(
    val productNumber: String,
    val quantity: Int
)
