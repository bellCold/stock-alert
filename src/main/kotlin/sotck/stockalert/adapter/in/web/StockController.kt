package sotck.stockalert.adapter.`in`.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sotck.stockalert.application.service.StockPriceMonitoringService
import sotck.stockalert.domain.stock.Stock
import sotck.stockalert.domain.stock.StockRepository
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/stocks")
class StockController(private val stockRepository: StockRepository, private val stockPriceMonitoringService: StockPriceMonitoringService) {

    @GetMapping("/{stockCode}")
    fun getStock(@PathVariable stockCode: String): ResponseEntity<StockResponse> {
        val stock = stockRepository.findByStockCode(stockCode)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(stock.toResponse())
    }

    @PostMapping("/{stockCode}/refresh")
    fun refreshStockPrice(@PathVariable stockCode: String): ResponseEntity<StockResponse> {
        stockPriceMonitoringService.updateStockPrice(stockCode)
        val stock = stockRepository.findByStockCode(stockCode)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(stock.toResponse())
    }

    @GetMapping
    fun getAllStocks(): ResponseEntity<List<StockResponse>> {
        val stocks = stockRepository.findAll()
        return ResponseEntity.ok(stocks.map { it.toResponse() })
    }
}

data class StockResponse(
    val id: Long?,
    val stockCode: String,
    val stockName: String,
    val currentPrice: BigDecimal,
    val highestPrice: BigDecimal,
    val lastUpdatedAt: String
)

fun Stock.toResponse() = StockResponse(
    id = this.id,
    stockCode = this.stockCode,
    stockName = this.stockName,
    currentPrice = this.currentPrice.value,
    highestPrice = this.highestPrice.value,
    lastUpdatedAt = this.updatedAt.toString()
)