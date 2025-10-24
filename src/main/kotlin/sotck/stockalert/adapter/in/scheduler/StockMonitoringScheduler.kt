package sotck.stockalert.adapter.`in`.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import sotck.stockalert.application.service.StockPriceMonitoringService
import sotck.stockalert.common.logger

@Component
class StockMonitoringScheduler(private val stockPriceMonitoringService: StockPriceMonitoringService) {
    private val logger = logger()

    @Scheduled(fixedDelayString = "\${monitoring.stock.update-interval:60}000")
    fun updateStockPrices() {
        try {
            logger.info("주식 가격 업데이트 시작")
            stockPriceMonitoringService.updateAllStockPrices()
            logger.info("주식 가격 업데이트 완료")
        } catch (e: Exception) {
            logger.error("주식 가격 업데이트 중 오류 발생", e)
        }
    }

    @Scheduled(fixedDelayString = "\${monitoring.stock.alert-check-interval:30}000")
    fun checkAlerts() {
        try {
            logger.info("알림 조건 체크 시작")
            stockPriceMonitoringService.checkAndNotifyAlerts()
            logger.info("알림 조건 체크 완료")
        } catch (e: Exception) {
            logger.error("알림 조건 체크 중 오류 발생", e)
        }
    }
}