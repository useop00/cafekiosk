package hello.kiosk.repository

import hello.kiosk.domain.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long> {

    fun findAllByProductNumberIn(productNumber: List<String>): List<Stock>
    fun findByProductNumber(productNumber: String): Stock?
}