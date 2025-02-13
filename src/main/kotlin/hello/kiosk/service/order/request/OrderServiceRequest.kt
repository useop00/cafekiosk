package hello.kiosk.service.order.request

import jakarta.validation.constraints.NotEmpty

data class OrderServiceRequest(

    val productNumber: List<String>,
    val productQuantities: Map<String, Int>
)