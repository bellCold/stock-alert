package sotck.stockalert.adapter.`in`.web.response

import sotck.stockalert.application.dto.SignInResult

data class SignInResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun from(result: SignInResult): SignInResponse {
            return SignInResponse(
                userId = result.userId,
                email = result.email,
                name = result.name,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        }
    }
}