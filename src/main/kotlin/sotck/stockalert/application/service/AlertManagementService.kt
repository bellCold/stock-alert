package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.common.exception.AlertNotFoundException
import sotck.stockalert.common.exception.StockNotFoundException
import sotck.stockalert.common.exception.UnauthorizedAlertAccessException
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertCondition
import sotck.stockalert.domain.alert.AlertRepository
import sotck.stockalert.domain.alert.AlertType
import sotck.stockalert.domain.stock.StockRepository
import java.math.BigDecimal

@Service
@Transactional
class AlertManagementService(private val alertRepository: AlertRepository, private val stockRepository: StockRepository) {

    fun createAlert(request: CreateAlertRequest): Alert {
        val stock = stockRepository.findByStockCode(request.stockCode) ?: throw StockNotFoundException(request.stockCode)

        val condition = when (request.alertType) {
            AlertType.TARGET_PRICE -> AlertCondition(
                targetPrice = request.targetPrice,
                isAbove = request.isAbove ?: true
            )

            AlertType.CHANGE_RATE -> AlertCondition(changeRateThreshold = request.changeRateThreshold)

            else -> AlertCondition()
        }

        val alert = Alert(
            stockId = stock.id!!,
            userId = request.userId,
            alertType = request.alertType,
            condition = condition
        )

        return alertRepository.save(alert)
    }

    @Transactional(readOnly = true)
    fun getUserAlerts(userId: Long): List<Alert> {
        return alertRepository.findByUserId(userId)
    }

    fun disableAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId)
            .firstOrNull { it.id == alertId }
            ?: throw UnauthorizedAlertAccessException(alertId, userId)

        alert.disable()
        alertRepository.save(alert)
    }

    fun deleteAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId)
            .firstOrNull { it.id == alertId }
            ?: throw UnauthorizedAlertAccessException(alertId, userId)

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