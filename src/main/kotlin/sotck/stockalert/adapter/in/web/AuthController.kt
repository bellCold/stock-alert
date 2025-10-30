package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sotck.stockalert.adapter.`in`.web.request.RefreshTokenRequest
import sotck.stockalert.adapter.`in`.web.request.SignInRequest
import sotck.stockalert.adapter.`in`.web.request.SignUpRequest
import sotck.stockalert.adapter.`in`.web.response.RefreshTokenResponse
import sotck.stockalert.adapter.`in`.web.response.SignInResponse
import sotck.stockalert.adapter.`in`.web.response.SignUpResponse
import sotck.stockalert.application.dto.SignInCommand
import sotck.stockalert.application.dto.SignUpCommand
import sotck.stockalert.application.port.`in`.RefreshAccessTokenUseCase
import sotck.stockalert.application.port.`in`.SignInUseCase
import sotck.stockalert.application.port.`in`.SignUpUseCase
import sotck.stockalert.common.response.ApiResponse

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ApiResponse<SignUpResponse> {
        val result = signUpUseCase.signUp(SignUpCommand.from(request))
        return ApiResponse.success(SignUpResponse.from(result))
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody request: SignInRequest): ApiResponse<SignInResponse> {
        val result = signInUseCase.signIn(SignInCommand.from(request))
        return ApiResponse.success(SignInResponse.from(result))
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ApiResponse<RefreshTokenResponse> {
        val result = refreshAccessTokenUseCase.refreshAccessToken(request.refreshToken)
        return ApiResponse.success(RefreshTokenResponse.from(result))
    }
}
