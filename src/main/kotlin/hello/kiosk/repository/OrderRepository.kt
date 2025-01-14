package hello.kiosk.repository

import hello.kiosk.domain.order.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
}