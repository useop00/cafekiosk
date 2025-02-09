package hello.kiosk.repository

import hello.kiosk.domain.stock.Stock
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface StockRepository : JpaRepository<Stock, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    fun findByProductNumber(productNumber: String): Stock?
}