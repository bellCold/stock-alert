package sotck.stockalert.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sotck.stockalert.domain.UserFixture

class UserTest {

    @Test
    fun `사용자를 생성하면 ACTIVE 상태이다`() {
        // given & when
        val user = UserFixture.create()

        // then
        assertThat(user.status).isEqualTo(UserStatus.ACTIVE)
        assertThat(user.isActive()).isTrue()
    }

    @Test
    fun `사용자를 비활성화하면 INACTIVE 상태가 된다`() {
        // given
        val user = UserFixture.create()

        // when
        user.deactivate()

        // then
        assertThat(user.status).isEqualTo(UserStatus.INACTIVE)
        assertThat(user.isActive()).isFalse()
    }

    @Test
    fun `비활성화된 사용자를 활성화하면 ACTIVE 상태가 된다`() {
        // given
        val user = UserFixture.inactive()

        // when
        user.activate()

        // then
        assertThat(user.status).isEqualTo(UserStatus.ACTIVE)
        assertThat(user.isActive()).isTrue()
    }

    @Test
    fun `SUSPENDED 상태의 사용자는 active 하지 않다`() {
        // given
        val user = UserFixture.suspended()

        // then
        assertThat(user.isActive()).isFalse()
    }

    @Test
    fun `사용자는 이메일과 이름을 가진다`() {
        // given
        val email = "test@example.com"
        val name = "홍길동"

        // when
        val user = UserFixture.create(email = email, name = name)

        // then
        assertThat(user.email).isEqualTo(email)
        assertThat(user.name).isEqualTo(name)
    }

    @Test
    fun  `사용자의 비밀번호를 변경할 수 있다`() {
        // given
        val user = UserFixture.create(password = "old_password")
        val newPassword = "new_hashed_password"

        // when
        user.password = newPassword

        // then
        assertThat(user.password).isEqualTo(newPassword)
    }
}
