package sotck.stockalert.domain.user

import jakarta.persistence.*
import sotck.stockalert.domain.BaseEntity

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_email", columnNames = ["email"])
    ]
)
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: Email,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    var password: String,  // 암호화된 비밀번호 (bcrypt hash)

    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE
) : BaseEntity() {
    init {
        require(name.isNotBlank()) { "이름은 빈 값일 수 없습니다." }
        require(name.length <= 50) { "이름은 최대 50자까지 입력 가능합니다." }
    }
    fun deactivate() {
        this.status = UserStatus.INACTIVE
    }

    fun activate() {
        this.status = UserStatus.ACTIVE
    }

    fun isActive(): Boolean = status == UserStatus.ACTIVE
}
