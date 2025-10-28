package sotck.stockalert.application.port.out

import sotck.stockalert.domain.stock.Price

interface StockDataPort {
    fun getCurrentPrice(stockCode: String): Price?
    fun getCurrentPrices(stockCodes: List<String>): Map<String, Price>
    fun getStockInfo(stockCode: String): StockData?
}

data class StockData(
    val stockCode: String,
    val stockName: String,
    val currentPrice: Price,
    val highestPrice: Price
)