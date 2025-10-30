package sotck.stockalert.adapter.`in`.web.response

import sotck.stockalert.application.dto.SignUpResult

data class SignUpResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun from(result: SignUpResult): SignUpResponse {
            return SignUpResponse(
                userId = result.userId,
                email = result.email,
                name = result.name,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        }
    }
}