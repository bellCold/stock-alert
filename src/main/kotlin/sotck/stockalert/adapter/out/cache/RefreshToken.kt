package sotck.stockalert.adapter.out.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.util.concurrent.TimeUnit

@RedisHash(value = "refreshToken")
data class RefreshToken(
    @Id
    val userId: Long,
    val token: String,
    @TimeToLive(unit = TimeUnit.SECONDS)
    val ttl: Long = 604800 // 7일
)
