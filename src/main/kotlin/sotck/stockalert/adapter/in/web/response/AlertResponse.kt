package sotck.stockalert.adapter.`in`.web.response

import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal
import java.time.LocalDateTime

data class AlertResponse(
    val id: Long?,
    val stockId: Long,
    val alertType: AlertType,
    val status: String,
    val targetPrice: BigDecimal?,
    val changeRateThreshold: BigDecimal?,
    val createdAt: LocalDateTime,
    val triggeredAt: LocalDateTime?
) {
    companion object {
        fun from(alert: Alert): AlertResponse {
            return AlertResponse(
                id = alert.id,
                stockId = alert.stockId,
                alertType = alert.alertType,
                status = alert.status.name,
                targetPrice = alert.condition.targetPrice,
                changeRateThreshold = alert.condition.changeRateThreshold,
                createdAt = alert.createdAt,
                triggeredAt = alert.triggeredAt
            )
        }
    }
}