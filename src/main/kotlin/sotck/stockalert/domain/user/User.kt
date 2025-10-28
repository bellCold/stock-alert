package sotck.stockalert.domain.user

import jakarta.persistence.*
import sotck.stockalert.domain.BaseEntity

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val email: String,
    val name: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE
) : BaseEntity() {
    fun deactivate() {
        this.status = UserStatus.INACTIVE
    }

    fun activate() {
        this.status = UserStatus.ACTIVE
    }

    fun isActive(): Boolean = status == UserStatus.ACTIVE
}
