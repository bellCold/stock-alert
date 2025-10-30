package sotck.stockalert.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PasswordTest {

    @Test
    fun `유효한 비밀번호로 Password 객체를 생성할 수 있다`() {
        // given
        val validPassword = "password123"

        // when
        val password = Password(validPassword)

        // then
        assertThat(password.value).isEqualTo(validPassword)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "12345678",
            "password",
            "password123",
            "MyP@ssw0rd",
        ]
    )
    fun `8자 이상의 비밀번호를 생성할 수 있다`(validPassword: String) {
        // when
        val password = Password(validPassword)

        // then
        assertThat(password.value).isEqualTo(validPassword)
    }

    @Test
    fun `빈 문자열로 Password 객체를 생성하면 예외가 발생한다`() {
        // when & then
        assertThatThrownBy { Password("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("비밀번호는 빈 값일 수 없습니다.")
    }

    @Test
    fun `공백 문자열로 Password 객체를 생성하면 예외가 발생한다`() {
        // when & then
        assertThatThrownBy { Password("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("비밀번호는 빈 값일 수 없습니다.")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "1234567",      // 7자
            "pass",         // 4자
            "a"             // 1자
        ]
    )
    fun `8자 미만의 비밀번호는 예외가 발생한다`(shortPassword: String) {
        // when & then
        assertThatThrownBy { Password(shortPassword) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.")
    }

    @Test
    fun `비밀번호 길이가 100자를 초과하면 예외가 발생한다`() {
        // given
        val longPassword = "a".repeat(101)

        // when & then
        assertThatThrownBy { Password(longPassword) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("비밀번호는 최대 100자까지 입력 가능합니다.")
    }

    @Test
    fun `정확히 100자의 비밀번호를 생성할 수 있다`() {
        // given
        val password100 = "a".repeat(100)

        // when
        val password = Password(password100)

        // then
        assertThat(password.value).hasSize(100)
    }
}
