package sotck.stockalert.application.dto

data class SignUpResult(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)

data class SignInResult(
    val userId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenResult(
    val accessToken: String
)