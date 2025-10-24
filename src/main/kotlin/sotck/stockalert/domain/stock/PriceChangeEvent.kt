package sotck.stockalert.domain.stock

import java.math.BigDecimal
import java.time.LocalDateTime

sealed class PriceChangeEvent(
    open val stock: Stock,
    open val oldPrice: Price,
    open val newPrice: Price,
    open val occurredAt: LocalDateTime = LocalDateTime.now()
) {
    data class NewHighPrice(
        override val stock: Stock,
        override val oldPrice: Price,
        override val newPrice: Price,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : PriceChangeEvent(stock, oldPrice, newPrice, occurredAt)

    data class Surge(
        override val stock: Stock,
        override val oldPrice: Price,
        override val newPrice: Price,
        val changeRate: BigDecimal,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : PriceChangeEvent(stock, oldPrice, newPrice, occurredAt)

    data class Fall(
        override val stock: Stock,
        override val oldPrice: Price,
        override val newPrice: Price,
        val changeRate: BigDecimal,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : PriceChangeEvent(stock, oldPrice, newPrice, occurredAt)
}