package sotck.stockalert.adapter.out.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import sotck.stockalert.application.port.out.StockData
import sotck.stockalert.application.port.out.StockDataPort
import sotck.stockalert.domain.stock.Price
import java.math.BigDecimal

/**
 * 한국투자증권 Open API 클라이언트
 * 실제 API 명세에 맞춰 구현 필요
 */
@Component
class KisApiClient(
    private val restTemplate: RestTemplate,
    @Value("\${stock.api.kis.base-url}") private val baseUrl: String,
    @Value("\${stock.api.kis.app-key}") private val appKey: String,
    @Value("\${stock.api.kis.app-secret}") private val appSecret: String
) : StockDataPort {

    private var accessToken: String? = null

    override fun getCurrentPrice(stockCode: String): Price? {
        return try {
            val headers = createHeaders()
            val url = "$baseUrl/uapi/domestic-stock/v1/quotations/inquire-price"

            val requestEntity = HttpEntity<Any>(headers)
            val params = mapOf(
                "fid_cond_mrkt_div_code" to "J",
                "fid_input_iscd" to stockCode
            )

            val response = restTemplate.exchange(
                "$url?${params.toQueryString()}",
                HttpMethod.GET,
                requestEntity,
                KisStockPriceResponse::class.java
            )

            response.body?.output?.stckPrpr?.let {
                Price(BigDecimal(it))
            }
        } catch (e: Exception) {
            // 로깅 및 에러 처리
            null
        }
    }

    override fun getCurrentPrices(stockCodes: List<String>): Map<String, Price> {
        return stockCodes.associateWith { code ->
            getCurrentPrice(code) ?: Price(BigDecimal.ZERO)
        }
    }

    override fun getStockInfo(stockCode: String): StockData? {
        return try {
            val headers = createHeaders()
            val url = "$baseUrl/uapi/domestic-stock/v1/quotations/inquire-price"

            val requestEntity = HttpEntity<Any>(headers)
            val params = mapOf(
                "fid_cond_mrkt_div_code" to "J",
                "fid_input_iscd" to stockCode
            )

            val response = restTemplate.exchange(
                "$url?${params.toQueryString()}",
                HttpMethod.GET,
                requestEntity,
                KisStockPriceResponse::class.java
            )

            response.body?.output?.let { output ->
                StockData(
                    stockCode = stockCode,
                    stockName = output.prdtAbrvName ?: "",
                    currentPrice = Price(BigDecimal(output.stckPrpr)),
                    highestPrice = Price(BigDecimal(output.stckHgpr ?: output.stckPrpr))
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            set("Content-Type", "application/json")
            set("authorization", "Bearer ${getAccessToken()}")
            set("appkey", appKey)
            set("appsecret", appSecret)
            set("tr_id", "FHKST01010100")
        }
    }

    private fun getAccessToken(): String {
        // 토큰 발급 로직 (실제 API 명세에 맞춰 구현)
        if (accessToken == null) {
            // 토큰 발급 API 호출
            accessToken = "dummy-token"
        }
        return accessToken!!
    }

    private fun Map<String, String>.toQueryString(): String {
        return this.entries.joinToString("&") { "${it.key}=${it.value}" }
    }
}

// API Response DTOs
data class KisStockPriceResponse(
    val rtCd: String,
    val msgCd: String,
    val msg1: String,
    val output: KisStockPriceOutput
)

data class KisStockPriceOutput(
    val stckPrpr: String,        // 주식 현재가
    val stckHgpr: String?,       // 주식 최고가
    val prdtAbrvName: String?    // 상품 약어명
)