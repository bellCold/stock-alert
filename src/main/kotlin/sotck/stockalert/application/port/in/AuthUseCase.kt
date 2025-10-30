package sotck.stockalert.application.port.`in`

import sotck.stockalert.application.dto.RefreshTokenResult
import sotck.stockalert.application.dto.SignInCommand
import sotck.stockalert.application.dto.SignInResult
import sotck.stockalert.application.dto.SignUpCommand
import sotck.stockalert.application.dto.SignUpResult

interface SignUpUseCase {
    fun signUp(command: SignUpCommand): SignUpResult
}

interface SignInUseCase {
    fun signIn(command: SignInCommand): SignInResult
}

interface RefreshAccessTokenUseCase {
    fun refreshAccessToken(refreshToken: String): RefreshTokenResult
}