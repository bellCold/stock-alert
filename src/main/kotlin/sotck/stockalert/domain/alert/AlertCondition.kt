package sotck.stockalert.domain.alert

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import sotck.stockalert.domain.stock.Price
import sotck.stockalert.domain.stock.Stock
import java.math.BigDecimal

@Embeddable
data class AlertCondition(
    @Column(name = "target_price")
    val targetPrice: BigDecimal? = null,

    @Column(name = "change_rate_threshold")
    val changeRateThreshold: BigDecimal? = null,

    @Column(name = "is_above")
    val isAbove: Boolean = true  // true: 이상, false: 이하
) {
    fun isSatisfied(stock: Stock): Boolean {
        return when {
            targetPrice != null -> checkTargetPrice(stock.currentPrice)
            changeRateThreshold != null -> checkChangeRate(stock)
            else -> false
        }
    }

    private fun checkTargetPrice(currentPrice: Price): Boolean {
        val target = targetPrice ?: return false
        return if (isAbove) {
            currentPrice.value >= target
        } else {
            currentPrice.value <= target
        }
    }

    private fun checkChangeRate(stock: Stock): Boolean {
        // 실제로는 이전 가격과 비교 로직 필요
        // 여기서는 간단한 예시
        return false
    }
}