package sotck.stockalert.domain.stock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sotck.stockalert.domain.StockFixture
import java.math.BigDecimal

class StockTest {

    @Test
    fun `가격 업데이트 시 변동 없으면 null을 반환한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "12000")
        val newPrice = Price(BigDecimal("10200")) // 2% 상승 (급등 임계값 5% 미만)

        // when
        val event = stock.updatePrice(newPrice)

        // then
        assertThat(event).isNull()
        assertThat(stock.currentPrice).isEqualTo(newPrice)
    }

    @Test
    fun `가격이 5% 이상 급등하면 Surge 이벤트를 발생한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "12000")
        val newPrice = Price(BigDecimal("10600")) // 6% 상승

        // when
        val event = stock.updatePrice(newPrice)

        // then
        assertThat(event).isInstanceOf(PriceChangeEvent.Surge::class.java)
        assertThat(stock.currentPrice).isEqualTo(newPrice)

        val surgeEvent = event as PriceChangeEvent.Surge
        assertThat(surgeEvent.changeRate.compareTo(BigDecimal("5.0"))).isGreaterThanOrEqualTo(0)
    }

    @Test
    fun `가격이 3% 이상 급락하면 Fall 이벤트를 발생한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "12000")
        val newPrice = Price(BigDecimal("9600")) // 4% 하락

        // when
        val event = stock.updatePrice(newPrice)

        // then
        assertThat(event).isInstanceOf(PriceChangeEvent.Fall::class.java)
        assertThat(stock.currentPrice).isEqualTo(newPrice)

        val fallEvent = event as PriceChangeEvent.Fall
        assertThat(fallEvent.changeRate.compareTo(BigDecimal("-3.0"))).isLessThanOrEqualTo(0)
    }

    @Test
    fun `신고가를 갱신하면 NewHighPrice 이벤트를 발생한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "11000")
        val newPrice = Price(BigDecimal("11500"))

        // when
        val event = stock.updatePrice(newPrice)

        // then
        assertThat(event).isInstanceOf(PriceChangeEvent.NewHighPrice::class.java)
        assertThat(stock.highestPrice).isEqualTo(newPrice)
        assertThat(stock.currentPrice).isEqualTo(newPrice)
    }

    @Test
    fun `신고가 갱신이 급등보다 우선순위가 높다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "11000")
        val newPrice = Price(BigDecimal("12000")) // 20% 상승 + 신고가

        // when
        val event = stock.updatePrice(newPrice)

        // then
        // 신고가 이벤트가 우선
        assertThat(event).isInstanceOf(PriceChangeEvent.NewHighPrice::class.java)
        assertThat(stock.highestPrice).isEqualTo(newPrice)
    }

    @Test
    fun `연속으로 가격을 업데이트할 수 있다`() {
        // given
        val stock = StockFixture.create(currentPrice = "10000", highestPrice = "11000")

        // when
        val event1 = stock.updatePrice(Price(BigDecimal("10600"))) // 6% 급등 (신고가 아님, 11000보다 낮음)
        val event2 = stock.updatePrice(Price(BigDecimal("11200"))) // 신고가 (11000보다 높음)
        val event3 = stock.updatePrice(Price(BigDecimal("10800"))) // 약 3.57% 하락

        // then
        assertThat(event1).isInstanceOf(PriceChangeEvent.Surge::class.java)
        assertThat(event2).isInstanceOf(PriceChangeEvent.NewHighPrice::class.java)
        assertThat(event3).isInstanceOf(PriceChangeEvent.Fall::class.java)
        assertThat(stock.currentPrice.value).isEqualTo(BigDecimal("10800"))
        assertThat(stock.highestPrice.value).isEqualTo(BigDecimal("11200"))
    }
}
