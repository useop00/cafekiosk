package hello.kiosk.domain.user

import hello.kiosk.domain.BaseEntity
import hello.kiosk.domain.order.Order
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var username: String,
    var password: String,
    var salt:String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orders: MutableList<Order> = mutableListOf()


) : BaseEntity() {

    companion object {
        fun create(username: String, password: String, salt: String): User {
            return User(
                username = username,
                password = password,
                salt = salt
            )
        }
    }
}