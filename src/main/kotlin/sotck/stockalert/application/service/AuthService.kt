package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.common.exception.InvalidCredentialsException
import sotck.stockalert.common.exception.UserAlreadyExistsException
import sotck.stockalert.common.security.JwtTokenProvider
import sotck.stockalert.common.security.PasswordEncoder
import sotck.stockalert.domain.user.Email
import sotck.stockalert.domain.user.Password
import sotck.stockalert.domain.user.User
import sotck.stockalert.domain.user.UserRepository

@Service
@Transactional
class AuthService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder, private val jwtTokenProvider: JwtTokenProvider) {

    fun signUp(request: SignUpRequest): SignUpResponse {
        val email = Email(request.email)

        if (userRepository.findByEmail(email.value) != null) {
            throw UserAlreadyExistsException(email.value)
        }

        val password = Password(request.password)
        val encodedPassword = passwordEncoder.encode(password.value)

        val user = User(
            email = email,
            name = request.name,
            password = encodedPassword
        )

        val savedUser = userRepository.save(user)

        val accessToken = jwtTokenProvider.generateAccessToken(savedUser.id, savedUser.email.value)
        val refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.id)

        return SignUpResponse(
            userId = savedUser.id,
            email = savedUser.email.value,
            name = savedUser.name,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun signIn(request: SignInRequest): SignInResponse {
        val email = Email(request.email)
        val user = userRepository.findByEmail(email.value) ?: throw InvalidCredentialsException()

        val password = Password(request.password)
        if (!passwordEncoder.matches(password.value, user.password)) {
            throw InvalidCredentialsException()
        }

        if (!user.isActive()) {
            throw InvalidCredentialsException()
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email.value)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id)

        return SignInResponse(
            userId = user.id,
            email = user.email.value,
            name = user.name,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refreshAccessToken(refreshToken: String): RefreshTokenResponse {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw InvalidCredentialsException()
        }

        val userId = jwtTokenProvider.getUserIdFromToken(refreshToken)

        val user = userRepository.findById(userId) ?: throw InvalidCredentialsException()

        val newAccessToken = jwtTokenProvider.generateAccessToken(user.id, user.email.value)

        return RefreshTokenResponse(
            accessToken = newAccessToken
        )
    }
}

data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String
)

data class SignUpResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignInResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String
)
