package sotck.stockalert.common.exception

sealed class StockAlertException(
    message: String,
    val errorCode: ErrorCode
) : RuntimeException(message)

class StockNotFoundException(stockCode: String) :
    StockAlertException(
        message = "Stock not found: $stockCode",
        errorCode = ErrorCode.STOCK_NOT_FOUND
    )

class AlertNotFoundException(alertId: Long) :
    StockAlertException(
        message = "Alert not found: $alertId",
        errorCode = ErrorCode.ALERT_NOT_FOUND
    )

class UnauthorizedAlertAccessException(alertId: Long, userId: Long) :
    StockAlertException(
        message = "User $userId is not authorized to access alert $alertId",
        errorCode = ErrorCode.UNAUTHORIZED_ALERT_ACCESS
    )

class InvalidAlertConditionException(message: String) :
    StockAlertException(
        message = message,
        errorCode = ErrorCode.INVALID_ALERT_CONDITION
    )

class InvalidUserIdException(message: String = "Invalid or missing user ID") :
    StockAlertException(
        message = message,
        errorCode = ErrorCode.INVALID_USER_ID
    )

class UserAlreadyExistsException(email: String) :
    StockAlertException(
        message = "User already exists with email: $email",
        errorCode = ErrorCode.USER_ALREADY_EXISTS
    )

class InvalidCredentialsException(message: String = "Invalid email or password") :
    StockAlertException(
        message = message,
        errorCode = ErrorCode.INVALID_CREDENTIALS
    )
