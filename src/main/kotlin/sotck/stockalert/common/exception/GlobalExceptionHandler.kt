package sotck.stockalert.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sotck.stockalert.common.logger
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = logger()

    @ExceptionHandler(StockAlertException::class)
    fun handleStockAlertException(
        ex: StockAlertException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        logException(ex.errorCode.logLevel, "StockAlertException occurred: ${ex.message}", ex)

        return createProblemDetailResponse(
            errorCode = ex.errorCode,
            detail = ex.message ?: ex.errorCode.message,
            requestURI = request.requestURI
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        logException(ErrorCode.INVALID_INPUT.logLevel, "IllegalArgumentException occurred: ${ex.message}", ex)

        return createProblemDetailResponse(
            errorCode = ErrorCode.INVALID_INPUT,
            detail = ex.message ?: ErrorCode.INVALID_INPUT.message,
            requestURI = request.requestURI
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        logException(ErrorCode.INTERNAL_SERVER_ERROR.logLevel, "Unexpected exception occurred: ${ex.message}", ex)

        return createProblemDetailResponse(
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
            detail = ErrorCode.INTERNAL_SERVER_ERROR.message,
            requestURI = request.requestURI
        )
    }

    private fun logException(logLevel: LogLevel, message: String, ex: Exception) {
        when (logLevel) {
            LogLevel.DEBUG -> log.debug(message, ex)
            LogLevel.INFO -> log.info(message, ex)
            LogLevel.WARN -> log.warn(message, ex)
            LogLevel.ERROR -> log.error(message, ex)
        }
    }

    private fun createProblemDetailResponse(
        errorCode: ErrorCode,
        detail: String,
        requestURI: String
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status, detail)
        problemDetail.type = URI.create("https://api.stockalert.com/errors/${errorCode.code}")
        problemDetail.title = errorCode.message
        problemDetail.instance = URI.create(requestURI)
        problemDetail.setProperty("errorCode", errorCode.code)

        return ResponseEntity
            .status(errorCode.status)
            .body(problemDetail)
    }
}
