package hello.kiosk.controller.stock.request

import hello.kiosk.service.stock.request.StockCreateServiceRequest
import jakarta.validation.constraints.NotEmpty

data class StockCreateRequest(

    @field:NotEmpty(message = "상품 번호를 입력해주세요.")
    val productNumber: String?,

    @field:NotEmpty(message = "입고 수량을 입력해주세요.")
    val quantity: Int?
) {
    fun toServiceRequest(): StockCreateServiceRequest {
        return StockCreateServiceRequest(
            productNumber = productNumber!!,
            quantity = quantity!!
        )
    }
}
