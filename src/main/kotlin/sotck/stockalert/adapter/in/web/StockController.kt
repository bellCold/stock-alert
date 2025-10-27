package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.*
import sotck.stockalert.application.service.StockPriceMonitoringService
import sotck.stockalert.application.service.StockQueryService
import sotck.stockalert.common.response.ApiResponse
import sotck.stockalert.domain.stock.Stock
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/stocks")
class StockController(private val stockQueryService: StockQueryService, private val stockPriceMonitoringService: StockPriceMonitoringService) {

    @GetMapping("/{stockCode}")
    fun getStock(@PathVariable stockCode: String): ApiResponse<StockResponse> {
        val stock = stockQueryService.getStock(stockCode)
        return ApiResponse.success(stock.toResponse())
    }

    @PostMapping("/{stockCode}/refresh")
    fun refreshStockPrice(@PathVariable stockCode: String): ApiResponse<StockResponse> {
        stockPriceMonitoringService.updateStockPrice(stockCode)
        val stock = stockQueryService.getStock(stockCode)
        return ApiResponse.success(stock.toResponse())
    }

    @GetMapping
    fun getAllStocks(): ApiResponse<List<StockResponse>> {
        val stocks = stockQueryService.getAllStocks()
        return ApiResponse.success(stocks.map { it.toResponse() })
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