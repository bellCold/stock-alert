package sotck.stockalert.domain.alert

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sotck.stockalert.domain.AlertConditionFixture
import sotck.stockalert.domain.AlertFixture
import sotck.stockalert.domain.StockFixture

class AlertTest {

    @Test
    fun `알림을 생성하면 ACTIVE 상태이다`() {
        // given
        val stock = StockFixture.create()
        val condition = AlertConditionFixture.targetPriceAbove()

        // when
        val alert = Alert(
            stock = stock,
            userId = 1L,
            alertType = AlertType.TARGET_PRICE,
            condition = condition
        )

        // then
        assertThat(alert.status).isEqualTo(AlertStatus.ACTIVE)
        assertThat(alert.triggeredAt).isNull()
    }

    @Test
    fun `목표가 이상 조건을 만족하면 true를 반환한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "65000")
        val condition = AlertConditionFixture.targetPriceAbove("60000")
        val alert = AlertFixture.create(stock = stock, condition = condition)

        // when
        val isSatisfied = alert.checkCondition()

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이상 조건을 만족하지 않으면 false를 반환한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "55000")
        val condition = AlertConditionFixture.targetPriceAbove("60000")
        val alert = AlertFixture.create(stock = stock, condition = condition)

        // when
        val isSatisfied = alert.checkCondition()

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `목표가 이하 조건을 만족하면 true를 반환한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "55000")
        val condition = AlertConditionFixture.targetPriceBelow("60000")
        val alert = AlertFixture.create(stock = stock, condition = condition)

        // when
        val isSatisfied = alert.checkCondition()

        // then
        assertThat(isSatisfied).isTrue()
    }

    @Test
    fun `목표가 이하 조건을 만족하지 않으면 false를 반환한다`() {
        // given
        val stock = StockFixture.create(currentPrice = "65000")
        val condition = AlertConditionFixture.targetPriceBelow("60000")
        val alert = AlertFixture.create(stock = stock, condition = condition)

        // when
        val isSatisfied = alert.checkCondition()

        // then
        assertThat(isSatisfied).isFalse()
    }

    @Test
    fun `알림을 트리거하면 TRIGGERED 상태가 되고 triggeredAt이 설정된다`() {
        // given
        val alert = AlertFixture.create()

        // when
        alert.trigger()

        // then
        assertThat(alert.status).isEqualTo(AlertStatus.TRIGGERED)
        assertThat(alert.triggeredAt).isNotNull()
    }

    @Test
    fun `알림을 비활성화하면 DISABLED 상태가 된다`() {
        // given
        val alert = AlertFixture.create()

        // when
        alert.disable()

        // then
        assertThat(alert.status).isEqualTo(AlertStatus.DISABLED)
    }

    @Test
    fun `트리거된 알림을 비활성화할 수 있다`() {
        // given
        val alert = AlertFixture.create()
        alert.trigger()

        // when
        alert.disable()

        // then
        assertThat(alert.status).isEqualTo(AlertStatus.DISABLED)
        assertThat(alert.triggeredAt).isNotNull() // triggeredAt은 유지됨
    }
}
