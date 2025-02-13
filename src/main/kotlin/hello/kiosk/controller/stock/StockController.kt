package hello.kiosk.controller.stock

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.stock.request.StockCreateRequest
import hello.kiosk.service.stock.StockService
import hello.kiosk.service.stock.request.StockCreateServiceRequest
import hello.kiosk.service.stock.response.StockResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stocks")
class StockController (
    private val stockService: StockService
) {

    @PostMapping
    fun createStock(@RequestBody request: StockCreateRequest): ApiResponse<StockResponse> {
        return ApiResponse.ok(stockService.upsertStock(request.toServiceRequest()))
    }

    @GetMapping
    fun getStocks(): ApiResponse<List<StockResponse>> {
        return ApiResponse.ok(stockService.getAllStocks())
    }
}