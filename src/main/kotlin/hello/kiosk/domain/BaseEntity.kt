package hello.kiosk.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.LocalDateTime

@MappedSuperclass
class BaseEntity(
    @Column(updatable = false)
    var createDateTime: LocalDateTime? = null,

    var updateDateTime: LocalDateTime? = null
) {
    @PrePersist
    fun onCreate(){
        createDateTime = LocalDateTime.now()
    }

    @PreUpdate
    fun onUpdate(){
        updateDateTime = LocalDateTime.now()
    }
}