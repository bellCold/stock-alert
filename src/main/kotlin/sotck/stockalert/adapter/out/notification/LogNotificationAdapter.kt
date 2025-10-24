package sotck.stockalert.adapter.out.notification

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sotck.stockalert.application.port.out.NotificationPort
import sotck.stockalert.domain.alert.Alert

/**
 * 로그 기반 알림 어댑터 (개발/테스트용)
 * 실제로는 이메일, SMS, Push 알림 등으로 대체
 */
@Component
class LogNotificationAdapter : NotificationPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun send(alert: Alert, message: String) {
        logger.info(
            """
            ========================================
            알림 발송
            ========================================
            사용자 ID: ${alert.userId}
            종목: ${alert.stock.stockName} (${alert.stock.stockCode})
            알림 유형: ${alert.alertType}
            메시지:
            $message
            ========================================
            """.trimIndent()
        )
    }
}