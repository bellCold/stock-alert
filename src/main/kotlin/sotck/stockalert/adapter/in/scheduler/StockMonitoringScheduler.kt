package sotck.stockalert.adapter.`in`.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import sotck.stockalert.application.service.StockPriceMonitoringService
import sotck.stockalert.common.logger
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class StockMonitoringScheduler(private val stockPriceMonitoringService: StockPriceMonitoringService) {
    private val logger = logger()

    companion object {
        private val MARKET_OPEN_TIME = LocalTime.of(9, 0)
        private val MARKET_CLOSE_TIME = LocalTime.of(15, 30)
    }

    @Scheduled(fixedDelayString = "\${monitoring.stock.update-interval:60}000")
    fun updateStockPrices() {
        executeIfMarketOpen("주식 가격 업데이트") {
            stockPriceMonitoringService.updateAllStockPrices()
        }
    }

    @Scheduled(fixedDelayString = "\${monitoring.stock.alert-check-interval:30}000")
    fun checkAlerts() {
        executeIfMarketOpen("알림 조건 체크") {
            stockPriceMonitoringService.checkAndNotifyAlerts()
        }
    }

    private fun executeIfMarketOpen(taskName: String, task: () -> Unit) {
        if (!isMarketOpen()) {
            logger.debug("장 마감 시간입니다. $taskName 를 건너뜁니다.")
            return
        }

        try {
            logger.info("$taskName 시작")
            task()
            logger.info("$taskName 완료")
        } catch (e: Exception) {
            logger.error("$taskName 중 오류 발생", e)
        }
    }

    private fun isMarketOpen(): Boolean {
        val now = LocalDateTime.now()
        val dayOfWeek = now.dayOfWeek
        val currentTime = now.toLocalTime()

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false
        }

        return currentTime.isAfter(MARKET_OPEN_TIME) && currentTime.isBefore(MARKET_CLOSE_TIME)
    }
}