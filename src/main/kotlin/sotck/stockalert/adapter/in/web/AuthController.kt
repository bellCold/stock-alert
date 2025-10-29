package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sotck.stockalert.application.service.*
import sotck.stockalert.common.response.ApiResponse

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = authService.signUp(request)
        return ApiResponse.success(response)
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody request: SignInRequest): ApiResponse<SignInResponse> {
        val response = authService.signIn(request)
        return ApiResponse.success(response)
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ApiResponse<RefreshTokenResponse> {
        val response = authService.refreshAccessToken(request.refreshToken)
        return ApiResponse.success(response)
    }
}

data class RefreshTokenRequest(
    val refreshToken: String
)
