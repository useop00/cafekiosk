package hello.kiosk.controller.order.request

import hello.kiosk.service.order.request.OrderServiceRequest
import jakarta.validation.constraints.NotEmpty

data class OrderRequest(

    @NotEmpty(message = "주문할 상품 번호를 입력해주세요.")
    val productNumber: List<String>,
    @NotEmpty(message = "주문할 상품 수량을 입력해주세요.")
    val productQuantities: Map<String, Int>
) {
    fun toServiceRequest(): OrderServiceRequest {
        return OrderServiceRequest(
            productNumber = productNumber,
            productQuantities = productQuantities
        )
    }
}