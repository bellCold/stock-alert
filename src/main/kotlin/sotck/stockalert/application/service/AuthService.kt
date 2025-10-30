package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.application.dto.RefreshTokenResult
import sotck.stockalert.application.dto.SignInCommand
import sotck.stockalert.application.dto.SignInResult
import sotck.stockalert.application.dto.SignUpCommand
import sotck.stockalert.application.dto.SignUpResult
import sotck.stockalert.application.port.`in`.RefreshAccessTokenUseCase
import sotck.stockalert.application.port.`in`.SignInUseCase
import sotck.stockalert.application.port.`in`.SignUpUseCase
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
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : SignUpUseCase, SignInUseCase, RefreshAccessTokenUseCase {

    override fun signUp(command: SignUpCommand): SignUpResult {
        val email = Email(command.email)

        if (userRepository.findByEmail(email.value) != null) {
            throw UserAlreadyExistsException(email.value)
        }

        val password = Password(command.password)
        val encodedPassword = passwordEncoder.encode(password.value)

        val user = User(
            email = email,
            name = command.name,
            password = encodedPassword
        )

        val savedUser = userRepository.save(user)

        val accessToken = jwtTokenProvider.generateAccessToken(savedUser.id, savedUser.email.value)
        val refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.id)

        return SignUpResult(
            userId = savedUser.id,
            email = savedUser.email.value,
            name = savedUser.name,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    override fun signIn(command: SignInCommand): SignInResult {
        val email = Email(command.email)
        val user = userRepository.findByEmail(email.value) ?: throw InvalidCredentialsException()

        val password = Password(command.password)
        if (!passwordEncoder.matches(password.value, user.password)) {
            throw InvalidCredentialsException()
        }

        if (!user.isActive()) {
            throw InvalidCredentialsException()
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email.value)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id)

        return SignInResult(
            userId = user.id,
            email = user.email.value,
            name = user.name,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    override fun refreshAccessToken(refreshToken: String): RefreshTokenResult {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw InvalidCredentialsException()
        }

        val userId = jwtTokenProvider.getUserIdFromToken(refreshToken)

        val user = userRepository.findById(userId) ?: throw InvalidCredentialsException()

        val newAccessToken = jwtTokenProvider.generateAccessToken(user.id, user.email.value)

        return RefreshTokenResult(
            accessToken = newAccessToken
        )
    }
}
