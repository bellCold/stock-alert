package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.application.dto.CreateAlertCommand
import sotck.stockalert.application.port.`in`.CreateAlertUseCase
import sotck.stockalert.application.port.`in`.DeleteAlertUseCase
import sotck.stockalert.application.port.`in`.DisableAlertUseCase
import sotck.stockalert.application.port.`in`.GetUserAlertsUseCase
import sotck.stockalert.common.exception.StockNotFoundException
import sotck.stockalert.common.exception.UnauthorizedAlertAccessException
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertCondition
import sotck.stockalert.domain.alert.AlertRepository
import sotck.stockalert.domain.alert.AlertType
import sotck.stockalert.domain.stock.StockRepository

@Service
@Transactional
class AlertManagementService(
    private val alertRepository: AlertRepository,
    private val stockRepository: StockRepository
) : CreateAlertUseCase, GetUserAlertsUseCase, DisableAlertUseCase, DeleteAlertUseCase {

    override fun createAlert(command: CreateAlertCommand): Alert {
        val stock = stockRepository.findByStockCode(command.stockCode) ?: throw StockNotFoundException(command.stockCode)

        val condition = when (command.alertType) {
            AlertType.TARGET_PRICE -> AlertCondition(
                targetPrice = command.targetPrice,
                isAbove = command.isAbove ?: true
            )

            AlertType.CHANGE_RATE -> AlertCondition(changeRateThreshold = command.changeRateThreshold)

            else -> AlertCondition()
        }

        val alert = Alert(
            stockId = stock.id!!,
            userId = command.userId,
            alertType = command.alertType,
            condition = condition
        )

        return alertRepository.save(alert)
    }

    @Transactional(readOnly = true)
    override fun getUserAlerts(userId: Long): List<Alert> {
        return alertRepository.findByUserId(userId)
    }

    override fun disableAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId).firstOrNull { it.id == alertId } ?: throw UnauthorizedAlertAccessException(alertId, userId)

        alert.disable()

        alertRepository.save(alert)
    }

    override fun deleteAlert(alertId: Long, userId: Long) {
        val alert = alertRepository.findByUserId(userId).firstOrNull { it.id == alertId } ?: throw UnauthorizedAlertAccessException(alertId, userId)

        alertRepository.delete(alert)
    }
}