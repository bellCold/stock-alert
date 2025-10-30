package sotck.stockalert.adapter.`in`.web.response

import sotck.stockalert.application.dto.RefreshTokenResult

data class RefreshTokenResponse(val accessToken: String) {
    companion object {
        fun from(result: RefreshTokenResult): RefreshTokenResponse {
            return RefreshTokenResponse(
                accessToken = result.accessToken
            )
        }
    }
}