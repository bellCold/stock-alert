package sotck.stockalert.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // Stock errors (1xxx)
    STOCK_NOT_FOUND(
        status = HttpStatus.NOT_FOUND,
        code = "STOCK-1001",
        message = "Stock not found"
    ),

    // Alert errors (2xxx)
    ALERT_NOT_FOUND(
        status = HttpStatus.NOT_FOUND,
        code = "ALERT-2001",
        message = "Alert not found"
    ),
    UNAUTHORIZED_ALERT_ACCESS(
        status = HttpStatus.FORBIDDEN,
        code = "ALERT-2002",
        message = "Unauthorized alert access"
    ),
    INVALID_ALERT_CONDITION(
        status = HttpStatus.BAD_REQUEST,
        code = "ALERT-2003",
        message = "Invalid alert condition"
    ),

    // Auth errors (3xxx)
    INVALID_USER_ID(
        status = HttpStatus.UNAUTHORIZED,
        code = "AUTH-3001",
        message = "Invalid or missing user ID"
    ),

    // Common errors (9xxx)
    INTERNAL_SERVER_ERROR(
        status = HttpStatus.INTERNAL_SERVER_ERROR,
        code = "COMMON-9001",
        message = "Internal server error"
    ),
    INVALID_INPUT(
        status = HttpStatus.BAD_REQUEST,
        code = "COMMON-9002",
        message = "Invalid input"
    )
}
