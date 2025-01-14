package hello.kiosk.repository

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllBySellingStatusIn(sellingStatuses: Collection<ProductSellingStatus>): List<Product>

    fun findAllByProductNumberIn(productNumbers: Collection<String>): List<Product>

    @Query(value = "select p.product_number from Product p order by id desc limit 1", nativeQuery = true)
    fun findLatestProduct(): String?
}