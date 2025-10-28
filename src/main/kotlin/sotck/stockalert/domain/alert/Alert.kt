package sotck.stockalert.domain.alert

import jakarta.persistence.*
import sotck.stockalert.domain.BaseEntity
import sotck.stockalert.domain.stock.Stock
import java.time.LocalDateTime

@Entity
@Table(name = "alert")
class Alert(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JoinColumn(name = "stock_id")
    val stockId: Long,

    @JoinColumn(name = "user_id")
    val userId: Long,

    @Enumerated(EnumType.STRING)
    val alertType: AlertType,

    @Embedded
    var condition: AlertCondition,

    @Enumerated(EnumType.STRING)
    var status: AlertStatus = AlertStatus.ACTIVE,

    var triggeredAt: LocalDateTime? = null
) : BaseEntity() {
    fun checkCondition(stock: Stock): Boolean {
        return condition.isSatisfied(stock)
    }

    fun trigger() {
        this.status = AlertStatus.TRIGGERED
        this.triggeredAt = LocalDateTime.now()
    }

    fun disable() {
        this.status = AlertStatus.DISABLED
    }
}