package hello.kiosk.controller.stock

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.stock.request.StockCreateRequest
import hello.kiosk.service.stock.StockService
import hello.kiosk.service.stock.response.StockResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stocks")
@Tag(name = "Stock", description = "재고 API")
class StockController (
    private val stockService: StockService
) {

    @Operation(summary = "재고 등록", description = "상품 타입이 BOTTLE, DESSERT일 경우 재고를 등록합니다.")
    @PostMapping
    fun createStock(@Valid @RequestBody request: StockCreateRequest): ApiResponse<StockResponse> {
        return ApiResponse.ok(stockService.upsertStock(request.toServiceRequest()))
    }

    @Operation(summary = "재고 조회", description = "재고를 조회합니다.")
    @GetMapping
    fun getStocks(): ApiResponse<List<StockResponse>> {
        return ApiResponse.ok(stockService.getAllStocks())
    }
}