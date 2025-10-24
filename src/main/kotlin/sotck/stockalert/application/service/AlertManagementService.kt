package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertCondition
import sotck.stockalert.domain.alert.AlertRepository
import sotck.stockalert.domain.alert.AlertType
import sotck.stockalert.domain.stock.StockRepository
import java.math.BigDecimal

/**
 * 알림 관리 Use Case
 */
@Service
@Transactional
class AlertManagementService(private val alertRepository: AlertRepository, private val stockRepository: StockRepository) {
    /**
     * 새로운 알림 생성
     */
    fun createAlert(request: CreateAlertRequest): Alert {
        val stock = stockRepository.findByStockCode(request.stockCode)
            ?: throw IllegalArgumentException("Stock not found: ${request.stockCode}")

        val condition = when (request.alertType) {
            AlertType.TARGET_PRICE -> AlertCondition(
                targetPrice = request.targetPrice,
                isAbove = request.isAbove ?: true
            )
            AlertType.CHANGE_RATE -> AlertCondition(
                changeRateThreshold = request.changeRateThreshold
            )
            else -> AlertCondition()
        }

        val alert = Alert(
            stock = stock,
            userId = request.userId,
            alertType = request.alertType,
            condition = condition
        )

        return alertRepository.save(alert)
    }

    /**
     * 사용자의 모든 알림 조회
     */
    @Transactional(readOnly = true)
    fun getUserAlerts(userId: Long): List<Alert> {
        return alertRepository.findByUserId(userId)
    }

    /**
     * 알림 비활성화
     */
    fun disableAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId)
            .firstOrNull { it.id == alertId }
            ?: throw IllegalArgumentException("Alert not found or unauthorized")

        alert.disable()
        alertRepository.save(alert)
    }

    /**
     * 알림 삭제
     */
    fun deleteAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId)
            .firstOrNull { it.id == alertId }
            ?: throw IllegalArgumentException("Alert not found or unauthorized")

        alertRepository.delete(alert)
    }
}

data class CreateAlertRequest(
    val userId: Long,
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal? = null,
    val changeRateThreshold: BigDecimal? = null,
    val isAbove: Boolean? = null
)