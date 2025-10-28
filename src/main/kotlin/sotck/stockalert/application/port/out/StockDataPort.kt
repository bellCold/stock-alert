package sotck.stockalert.application.port.out

import sotck.stockalert.domain.stock.Price

interface StockDataPort {
    /**
     * 실시간 주식 가격 조회
     */
    fun getCurrentPrice(stockCode: String): Price?

    /**
     * 여러 종목의 실시간 가격 조회
     */
    fun getCurrentPrices(stockCodes: List<String>): Map<String, Price>

    /**
     * 주식 상세 정보 조회
     */
    fun getStockInfo(stockCode: String): StockData?
}

data class StockData(
    val stockCode: String,
    val stockName: String,
    val currentPrice: Price,
    val highestPrice: Price
)