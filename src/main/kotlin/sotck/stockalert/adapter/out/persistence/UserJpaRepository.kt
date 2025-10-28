package sotck.stockalert.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import sotck.stockalert.domain.user.User

interface UserJpaRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}
