package sotck.stockalert.domain.stock

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceTest {

    @Test
    fun `가격은 0 이상이어야 한다`() {
        // given
        val negativeValue = BigDecimal("-100")

        // when & then
        assertThatThrownBy { Price(negativeValue) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("가격은 0 이상이어야 합니다.")
    }

    @Test
    fun `0원 가격을 생성할 수 있다`() {
        // given & when
        val price = Price(BigDecimal.ZERO)

        // then
        assertThat(price.value).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `두 가격을 비교할 수 있다 - 높은 가격`() {
        // given
        val price1 = Price(BigDecimal("10000"))
        val price2 = Price(BigDecimal("5000"))

        // when & then
        assertThat(price1.isHigherThan(price2)).isTrue()
        assertThat(price2.isHigherThan(price1)).isFalse()
    }

    @Test
    fun `두 가격을 비교할 수 있다 - 낮은 가격`() {
        // given
        val price1 = Price(BigDecimal("5000"))
        val price2 = Price(BigDecimal("10000"))

        // when & then
        assertThat(price1.isLowerThan(price2)).isTrue()
        assertThat(price2.isLowerThan(price1)).isFalse()
    }

    @Test
    fun `두 가격을 더할 수 있다`() {
        // given
        val price1 = Price(BigDecimal("5000"))
        val price2 = Price(BigDecimal("3000"))

        // when
        val result = price1 + price2

        // then
        assertThat(result.value).isEqualTo(BigDecimal("8000"))
    }

    @Test
    fun `두 가격을 뺄 수 있다`() {
        // given
        val price1 = Price(BigDecimal("10000"))
        val price2 = Price(BigDecimal("3000"))

        // when
        val result = price1 - price2

        // then
        assertThat(result).isEqualTo(BigDecimal("7000"))
    }

    @Test
    fun `Price ZERO 상수를 사용할 수 있다`() {
        // when
        val zero = Price.ZERO

        // then
        assertThat(zero.value).isEqualTo(BigDecimal.ZERO)
    }
}
