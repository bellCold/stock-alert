package sotck.stockalert.common.response

import java.time.Instant

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T,
    val timestamp: Instant = Instant.now()
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(data = data)
    }
}
