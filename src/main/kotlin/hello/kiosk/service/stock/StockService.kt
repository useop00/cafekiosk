package hello.kiosk.service.stock

import hello.kiosk.domain.stock.Stock
import hello.kiosk.repository.StockRepository
import hello.kiosk.service.stock.request.StockCreateRequest
import hello.kiosk.service.stock.response.StockResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StockService(
    private val stockRepository: StockRepository
) {

    @Transactional
    fun upsertStock(request: StockCreateRequest): StockResponse {
        val stock = stockRepository.findByProductNumber(request.productNumber)

        return if (stock == null) {
            val newStock = Stock.create(
                productNumber = request.productNumber,
                quantity = request.quantity
            )
            val savedStock = stockRepository.save(newStock)
            StockResponse.of(savedStock)
        } else {
            stock.increaseQuantity(request.quantity)
            StockResponse.of(stock)
        }

    }

    fun getAllStocks(): List<StockResponse> {
        return stockRepository.findAll().map { StockResponse.of(it) }
    }
}