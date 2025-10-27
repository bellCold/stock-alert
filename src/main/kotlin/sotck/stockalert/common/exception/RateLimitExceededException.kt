package sotck.stockalert.common.exception

class RateLimitExceededException(message: String) : StockAlertException(
    message = message,
    errorCode = ErrorCode.RATE_LIMIT_EXCEEDED
)
