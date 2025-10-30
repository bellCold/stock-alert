package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.application.port.`in`.UpdateStockPriceUseCase
import sotck.stockalert.application.port.out.NotificationPort
import sotck.stockalert.application.port.out.StockDataPort
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertRepository
import sotck.stockalert.domain.stock.PriceChangeEvent
import sotck.stockalert.domain.stock.Stock
import sotck.stockalert.domain.stock.StockRepository

@Service
@Transactional
class StockPriceMonitoringService(
    private val stockRepository: StockRepository,
    private val alertRepository: AlertRepository,
    private val stockDataPort: StockDataPort,
    private val notificationPort: NotificationPort
) : UpdateStockPriceUseCase {
    fun checkAndNotifyAlerts() {
        val activeAlerts = alertRepository.findActiveAlerts()

        activeAlerts.forEach { alert ->
            val stock = stockRepository.findById(alert.stockId) ?: return@forEach

            if (alert.checkCondition(stock)) {
                // 알림 발송
                val message = createAlertMessage(alert, stock)
                notificationPort.send(alert, message)

                // 알림 상태 업데이트
                alert.trigger()
                alertRepository.save(alert)
            }
        }
    }

    override fun updateStockPrice(stockCode: String) {
        val stock = stockRepository.findByStockCode(stockCode) ?: throw IllegalArgumentException("Stock not found: $stockCode")

        val currentPrice = stockDataPort.getCurrentPrice(stockCode) ?: throw IllegalStateException("Failed to fetch price for: $stockCode")

        val event = stock.updatePrice(currentPrice)
        stockRepository.save(stock)

        // 이벤트 기반 알림 처리
        event?.let { handlePriceChangeEvent(it) }
    }

    fun updateAllStockPrices() {
        val stocks = stockRepository.findAll()
        val stockCodes = stocks.map { it.stockCode }

        val prices = stockDataPort.getCurrentPrices(stockCodes)

        stocks.forEach { stock ->
            prices[stock.stockCode]?.let { price ->
                stock.updatePrice(price)
            }
        }

        stockRepository.saveAll(stocks)
    }

    private fun handlePriceChangeEvent(event: PriceChangeEvent) {
        // 이벤트에 따른 추가 로직 처리
        // 예: 로그 기록, 실시간 알림 등
    }

    private fun createAlertMessage(alert: Alert, stock: Stock): String {
        return """
            [주식 알림]
            종목: ${stock.stockName} (${stock.stockCode})
            현재가: ${stock.currentPrice.value}원
            알림 유형: ${alert.alertType}
        """.trimIndent()
    }
}