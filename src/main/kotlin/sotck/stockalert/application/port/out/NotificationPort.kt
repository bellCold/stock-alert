package sotck.stockalert.application.port.out

import sotck.stockalert.domain.alert.Alert

/**
 * 알림 전송을 위한 Port (이메일, SMS, Push 등)
 */
interface NotificationPort {
    fun send(alert: Alert, message: String)
}