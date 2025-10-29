package sotck.stockalert.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {

    @Test
    fun `유효한 이메일로 Email 객체를 생성할 수 있다`() {
        // given
        val validEmail = "test@example.com"

        // when
        val email = Email(validEmail)

        // then
        assertThat(email.value).isEqualTo(validEmail)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "user@domain.com",
        "test.user@example.co.kr",
        "test+tag@example.com",
        "user_123@test-domain.com"
    ])
    fun `다양한 형식의 유효한 이메일을 생성할 수 있다`(validEmail: String) {
        // when
        val email = Email(validEmail)

        // then
        assertThat(email.value).isEqualTo(validEmail)
    }

    @Test
    fun `빈 문자열로 Email 객체를 생성하면 예외가 발생한다`() {
        // when & then
        assertThatThrownBy { Email("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("이메일은 빈 값일 수 없습니다")
    }

    @Test
    fun `공백 문자열로 Email 객체를 생성하면 예외가 발생한다`() {
        // when & then
        assertThatThrownBy { Email("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("이메일은 빈 값일 수 없습니다")
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "invalid",
        "invalid@",
        "@invalid.com",
        "invalid@domain",
        "invalid.domain.com",
        "invalid @domain.com",
        "invalid@domain .com"
    ])
    fun `유효하지 않은 이메일 형식이면 예외가 발생한다`(invalidEmail: String) {
        // when & then
        assertThatThrownBy { Email(invalidEmail) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("유효하지 않은 이메일 형식입니다")
    }

    @Test
    fun `이메일 길이가 255자를 초과하면 예외가 발생한다`() {
        // given
        val longEmail = "a".repeat(256) + "@example.com"

        // when & then
        assertThatThrownBy { Email(longEmail) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("이메일은 최대 255자까지 입력 가능합니다")
    }
}
