package sotck.stockalert.domain.alert

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sotck.stockalert.domain.stock.Price
import sotck.stockalert.domain.stock.Stock
import java.math.BigDecimal

class AlertConditionTest {

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가와 같으면 만족한다`() {
        // given
        val stock = createStock(currentPrice = "60000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = true)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가보다 높으면 만족한다`() {
        // given
        val stock = createStock(currentPrice = "65000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = true)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가보다 낮으면 만족하지 않는다`() {
        // given
        val stock = createStock(currentPrice = "55000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = true)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가와 같으면 만족한다`() {
        // given
        val stock = createStock(currentPrice = "60000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = false)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가보다 낮으면 만족한다`() {
        // given
        val stock = createStock(currentPrice = "55000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = false)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가보다 높으면 만족하지 않는다`() {
        // given
        val stock = createStock(currentPrice = "65000")
        val condition = AlertCondition(targetPrice = BigDecimal("60000"), isAbove = false)

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `조건이 설정되지 않으면 false를 반환한다`() {
        // given
        val stock = createStock()
        val condition = AlertCondition()

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `변동률 조건은 현재 구현되지 않아 false를 반환한다`() {
        // given
        val stock = createStock()
        val condition = AlertCondition(changeRateThreshold = BigDecimal("5.0"))

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    private fun createStock(
        stockCode: String = "005930",
        stockName: String = "삼성전자",
        currentPrice: String = "60000"
    ): Stock {
        return Stock(
            stockCode = stockCode,
            stockName = stockName,
            currentPrice = Price(BigDecimal(currentPrice)),
            highestPrice = Price(BigDecimal(currentPrice))
        )
    }
}
