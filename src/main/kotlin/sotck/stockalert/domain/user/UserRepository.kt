package sotck.stockalert.domain.user

interface UserRepository {
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun existsByEmail(email: String): Boolean
}
