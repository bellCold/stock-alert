package sotck.stockalert.adapter.`in`.web.response

import sotck.stockalert.domain.stock.Stock
import java.math.BigDecimal

data class StockResponse(
    val id: Long?,
    val stockCode: String,
    val stockName: String,
    val currentPrice: BigDecimal,
    val highestPrice: BigDecimal,
    val lastUpdatedAt: String
) {
    companion object {
        fun from(stock: Stock): StockResponse {
            return StockResponse(
                id = stock.id,
                stockCode = stock.stockCode,
                stockName = stock.stockName,
                currentPrice = stock.currentPrice.value,
                highestPrice = stock.highestPrice.value,
                lastUpdatedAt = stock.updatedAt.toString()
            )
        }
    }
}