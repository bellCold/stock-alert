package sotck.stockalert.domain.stock

import java.math.BigDecimal

@JvmInline
value class Price(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO) { "가격은 0 이상이어야 합니다." }
    }

    fun isHigherThan(other: Price): Boolean = this.value > other.value

    fun isLowerThan(other: Price): Boolean = this.value < other.value

    operator fun minus(other: Price): BigDecimal = this.value - other.value

    operator fun plus(other: Price): Price = Price(this.value + other.value)

    companion object {
        val ZERO = Price(BigDecimal.ZERO)
    }
}