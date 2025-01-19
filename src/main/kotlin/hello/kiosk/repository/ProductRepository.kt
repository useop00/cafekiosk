package hello.kiosk.repository

import hello.kiosk.domain.product.Product
import hello.kiosk.domain.product.ProductSellingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllBySellingStatusIn(sellingStatuses: Collection<ProductSellingStatus>): List<Product>

    fun findAllByProductNumberIn(productNumbers: Collection<String>): List<Product>

    @Query(value = "select p.product_number from Product p " +
            "where p.product_number LIKE CONCAT(:prefix, '-%')" +
            "order by id desc limit 1", nativeQuery = true)
    fun findLatestProduct(prefix: String): String?

    @Query(value = "select * from Product p where p.product_number like %:prefix%", nativeQuery = true)
    fun findByProductNumberByPrefix(prefix: String): List<Product>
}