package sotck.stockalert.application.port.`in`

import sotck.stockalert.application.dto.CreateAlertCommand
import sotck.stockalert.domain.alert.Alert

interface CreateAlertUseCase {
    fun createAlert(command: CreateAlertCommand): Alert
}

interface GetUserAlertsUseCase {
    fun getUserAlerts(userId: Long): List<Alert>
}

interface DisableAlertUseCase {
    fun disableAlert(alertId: Long, userId: Long)
}

interface DeleteAlertUseCase {
    fun deleteAlert(alertId: Long, userId: Long)
}