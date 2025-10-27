package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sotck.stockalert.common.response.ApiResponse
import java.time.Instant

@RestController
@RequestMapping("/api/v1")
class PingController {

    @GetMapping("/ping")
    fun ping(): ApiResponse<PingResponse> {
        return ApiResponse.success(
            PingResponse(
                message = "pong",
                timestamp = Instant.now()
            )
        )
    }
}

data class PingResponse(
    val message: String,
    val timestamp: Instant
)
