package sotck.stockalert.adapter.out.api

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import sotck.stockalert.application.port.out.StockData
import sotck.stockalert.application.port.out.StockDataPort
import sotck.stockalert.domain.stock.Price

@Component
class NaverApiClient(private val webClient: WebClient) : StockDataPort {

    override fun getCurrentPrice(stockCode: String): Price? {
        return runCatching {
            val response = webClient.get()
                .uri("/api/realtime/domestic/stock/{code}", stockCode)
                .retrieve()
                .bodyToMono(NaverStockResponse::class.java)
                .block() ?: return null

            val priceText = response.datas.firstOrNull()?.closePrice ?: return null
            val price = priceText.replace(",", "").toBigDecimalOrNull() ?: return null

            Price(price)
        }.getOrNull()
    }

    override fun getCurrentPrices(stockCodes: List<String>): Map<String, Price> {
        return stockCodes.mapNotNull { stockCode ->
            getCurrentPrice(stockCode)?.let { stockCode to it }
        }.toMap()
    }

    override fun getStockInfo(stockCode: String): StockData? {
        return null  // 현재는 사용하지 않음
    }
}

data class NaverStockResponse(
    val pollingInterval: Int,
    val datas: List<NaverStockData>,
    val time: String
)

data class NaverStockData(
    val itemCode: String,
    val stockName: String,
    val closePrice: String,
    val compareToPreviousClosePrice: String,
    val fluctuationsRatio: String,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val accumulatedTradingVolume: String
)