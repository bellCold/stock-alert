package sotck.stockalert.adapter.out.persistence

import org.springframework.stereotype.Repository
import sotck.stockalert.domain.user.User
import sotck.stockalert.domain.user.UserRepository

@Repository
class UserRepositoryAdapter(private val userJpaRepository: UserJpaRepository) : UserRepository {

    override fun findById(id: Long): User? {
        return userJpaRepository.findById(id).orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)
    }

    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }
}
