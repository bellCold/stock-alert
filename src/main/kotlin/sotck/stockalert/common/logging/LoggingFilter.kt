package sotck.stockalert.common.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import sotck.stockalert.common.logger
import java.util.*

/**
 * HTTP 요청/응답 로깅 필터
 *
 * 주요 기능:
 * 1. Correlation ID 생성 및 MDC 컨텍스트 관리
 * 2. Request/Response 정보 로깅
 * 3. 처리 시간 측정
 */
@Component
class LoggingFilter : OncePerRequestFilter() {
    private val log = logger()

    companion object {
        private const val CORRELATION_ID_HEADER = "X-Correlation-ID"
        private const val CORRELATION_ID_KEY = "correlationId"
        private const val USER_ID_HEADER = "X-User-Id"
        private const val USER_ID_KEY = "userId"
        private const val ANONYMOUS = "anonymous"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (shouldSkipLogging(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val context = createLoggingContext(request, response)

        setupMDC(context)
        try {
            processRequest(context, filterChain)
        } finally {
            clearMDC()
        }
    }

    private fun shouldSkipLogging(request: HttpServletRequest): Boolean {
        return request.requestURI.startsWith("/actuator")
    }

    private fun createLoggingContext(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): LoggingContext {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)
        val correlationId = extractCorrelationId(request)
        val userId = extractUserId(request)

        return LoggingContext(
            request = wrappedRequest,
            response = wrappedResponse,
            correlationId = correlationId,
            userId = userId
        )
    }

    private fun extractCorrelationId(request: HttpServletRequest): String {
        return request.getHeader(CORRELATION_ID_HEADER) ?: generateCorrelationId()
    }

    private fun generateCorrelationId(): String = UUID.randomUUID().toString()

    private fun extractUserId(request: HttpServletRequest): String {
        return request.getHeader(USER_ID_HEADER) ?: ANONYMOUS
    }

    private fun setupMDC(context: LoggingContext) {
        MDC.put(CORRELATION_ID_KEY, context.correlationId)

        if (context.userId != ANONYMOUS) {
            MDC.put(USER_ID_KEY, context.userId)
        }

        context.response.setHeader(CORRELATION_ID_HEADER, context.correlationId)
    }

    private fun clearMDC() {
        MDC.clear()
    }

    private fun processRequest(context: LoggingContext, filterChain: FilterChain) {
        logRequest(context)

        val startTime = System.currentTimeMillis()
        filterChain.doFilter(context.request, context.response)
        val duration = System.currentTimeMillis() - startTime

        logResponse(context, duration)
        context.response.copyBodyToResponse()
    }

    private fun logRequest(context: LoggingContext) {
        val request = context.request

        log.info(
            "[REQUEST] {} {} | correlationId={} | userId={} | client={}",
            request.method,
            request.requestURI,
            context.correlationId,
            context.userId,
            request.remoteAddr
        )

        if (log.isDebugEnabled && request.queryString != null) {
            log.debug(
                "  Query: {} | ContentType: {} | ContentLength: {}",
                request.queryString,
                request.contentType ?: "N/A",
                request.contentLength
            )
        }
    }

    private fun logResponse(context: LoggingContext, duration: Long) {
        val response = context.response

        log.info(
            "[RESPONSE] {} | {}ms | contentType={}",
            response.status,
            duration,
            response.contentType ?: "N/A"
        )

        if (log.isDebugEnabled && response.status >= 400) {
            val body = getResponseBody(response)
            if (body.isNotEmpty()) {
                log.debug("  Response Body: {}", body)
            }
        }
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String {
        val content = response.contentAsByteArray
        return if (content.isNotEmpty()) {
            String(content, Charsets.UTF_8).take(500) // 최대 500자까지만
        } else {
            ""
        }
    }

    private data class LoggingContext(
        val request: ContentCachingRequestWrapper,
        val response: ContentCachingResponseWrapper,
        val correlationId: String,
        val userId: String
    )
}
