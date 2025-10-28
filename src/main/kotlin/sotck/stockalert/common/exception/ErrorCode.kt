package sotck.stockalert.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
    val logLevel: LogLevel
) {
    // STOCK
    STOCK_NOT_FOUND(status = HttpStatus.NOT_FOUND, code = "STOCK-1001", message = "Stock not found", logLevel = LogLevel.WARN),

    // ALERT
    ALERT_NOT_FOUND(status = HttpStatus.NOT_FOUND, code = "ALERT-2001", message = "Alert not found", logLevel = LogLevel.WARN),
    UNAUTHORIZED_ALERT_ACCESS(status = HttpStatus.FORBIDDEN, code = "ALERT-2002", message = "Unauthorized alert access", logLevel = LogLevel.WARN),
    INVALID_ALERT_CONDITION(status = HttpStatus.BAD_REQUEST, code = "ALERT-2003", message = "Invalid alert condition", logLevel = LogLevel.WARN),

    // AUTH
    INVALID_USER_ID(status = HttpStatus.UNAUTHORIZED, code = "AUTH-3001", message = "Invalid or missing user ID", logLevel = LogLevel.WARN),
    USER_ALREADY_EXISTS(status = HttpStatus.CONFLICT, code = "AUTH-3002", message = "User already exists", logLevel = LogLevel.WARN),
    INVALID_CREDENTIALS(status = HttpStatus.UNAUTHORIZED, code = "AUTH-3003", message = "Invalid credentials", logLevel = LogLevel.WARN),

    // COMMON
    INTERNAL_SERVER_ERROR(status = HttpStatus.INTERNAL_SERVER_ERROR, code = "COMMON-9001", message = "Internal server error", logLevel = LogLevel.ERROR),
    INVALID_INPUT(status = HttpStatus.BAD_REQUEST, code = "COMMON-9002", message = "Invalid input", logLevel = LogLevel.WARN),
    RATE_LIMIT_EXCEEDED(status = HttpStatus.TOO_MANY_REQUESTS, code = "COMMON-9003", message = "Too many requests", logLevel = LogLevel.WARN)
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}
