package sotck.stockalert.domain.alert

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sotck.stockalert.domain.AlertConditionFixture
import sotck.stockalert.domain.StockFixture

class AlertConditionTest {

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가와 같으면 만족한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "60000")
        val condition = AlertConditionFixture.targetPriceAbove("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가보다 높으면 만족한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "65000")
        val condition = AlertConditionFixture.targetPriceAbove("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이상 조건 - 현재가가 목표가보다 낮으면 만족하지 않는다`() {
        // given
        val stock = StockFixture.create(currentPrice = "55000")
        val condition = AlertConditionFixture.targetPriceAbove("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가와 같으면 만족한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "60000")
        val condition = AlertConditionFixture.targetPriceBelow("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가보다 낮으면 만족한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "55000")
        val condition = AlertConditionFixture.targetPriceBelow("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이하 조건 - 현재가가 목표가보다 높으면 만족하지 않는다`() {
        // given
        val stock = StockFixture.create(currentPrice = "65000")
        val condition = AlertConditionFixture.targetPriceBelow("60000")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `조건이 설정되지 않으면 false를 반환한다`() {
        // given
        val stock = StockFixture.create()
        val condition = AlertConditionFixture.empty()

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `변동률 조건은 현재 구현되지 않아 false를 반환한다`() {
        // given
        val stock = StockFixture.create()
        val condition = AlertConditionFixture.changeRate("5.0")

        // when
        val isSatisfied = condition.isSatisfied(stock)

        // then
        assertThat(isSatisfied).isFalse()
    }
}
