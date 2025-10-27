package sotck.stockalert.common.ratelimit

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import sotck.stockalert.common.exception.RateLimitExceededException
import sotck.stockalert.common.logger
import java.time.Duration

@Component
class RateLimitInterceptor(
    private val redisTemplate: StringRedisTemplate
) : HandlerInterceptor {
    private val log = logger()

    companion object {
        private const val USER_ID_HEADER = "X-User-Id"
        private const val RATE_LIMIT_PREFIX = "rate_limit:"
        private const val DEFAULT_RATE_LIMIT_SECONDS = 3L
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }

        if (shouldApplyRateLimitByMethod(request.method)) {
            checkRateLimit(request, DEFAULT_RATE_LIMIT_SECONDS)
        }

        return true
    }

    private fun shouldApplyRateLimitByMethod(method: String): Boolean {
        return method in setOf("POST", "PATCH")
    }

    private fun checkRateLimit(request: HttpServletRequest, seconds: Long) {
        val userId = request.getHeader(USER_ID_HEADER) ?: "anonymous"
        val key = generateKey(userId, request.method, request.requestURI)


        val isAllowed = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(seconds))

        if (isAllowed == false) {
            val ttl = redisTemplate.getExpire(key)

            log.warn("Rate limit exceeded - userId={}, uri={}, remainingSeconds={}", userId, request.requestURI, ttl)

            throw RateLimitExceededException("Too many requests. Please try again in $ttl seconds.")
        }
    }

    private fun generateKey(userId: String, method: String, uri: String): String {
        return "$RATE_LIMIT_PREFIX$userId:$method:$uri"
    }
}
