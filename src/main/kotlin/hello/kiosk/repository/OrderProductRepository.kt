package hello.kiosk.repository

import hello.kiosk.domain.orderProduct.OrderProduct
import org.springframework.data.jpa.repository.JpaRepository

interface OrderProductRepository : JpaRepository<OrderProduct, Long> {
}