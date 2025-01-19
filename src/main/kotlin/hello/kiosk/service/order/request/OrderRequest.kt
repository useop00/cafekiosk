package hello.kiosk.service.order.request

data class OrderRequest(

    val productNumber: List<String>,
    val productQuantities: Map<String, Int>
)