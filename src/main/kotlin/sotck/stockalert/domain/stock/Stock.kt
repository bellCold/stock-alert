package sotck.stockalert.domain.stock

import jakarta.persistence.*
import sotck.stockalert.domain.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "stock",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_stock_code", columnNames = ["stock_code"])
    ]
)
class Stock(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "stock_code", nullable = false)
    val stockCode: String,

    val stockName: String,

    val marketType: String,

    var currentPrice: Price,

    var highestPrice: Price
) : BaseEntity() {
    fun updatePrice(newPrice: Price): PriceChangeEvent? {
        val oldPrice = this.currentPrice
        this.currentPrice = newPrice

        // 신고가 갱신 체크
        if (newPrice.isHigherThan(highestPrice)) {
            highestPrice = newPrice
            return PriceChangeEvent.NewHighPrice(this, oldPrice, newPrice)
        }

        // 급등/급락 체크
        val changeRate = calculateChangeRate(oldPrice, newPrice)
        return when {
            changeRate >= SURGE_THRESHOLD -> PriceChangeEvent.Surge(this, oldPrice, newPrice, changeRate)
            changeRate <= FALL_THRESHOLD.negate() -> PriceChangeEvent.Fall(this, oldPrice, newPrice, changeRate)
            else -> null
        }
    }

    private fun calculateChangeRate(oldPrice: Price, newPrice: Price): BigDecimal {
        return ((newPrice.value - oldPrice.value).divide(oldPrice.value, 10, java.math.RoundingMode.HALF_UP)) * BigDecimal(100)
    }

    companion object {
        private val SURGE_THRESHOLD = BigDecimal("5.0") // 5% 이상 급등
        private val FALL_THRESHOLD = BigDecimal("3.0")  // 3% 이상 급락
    }
}