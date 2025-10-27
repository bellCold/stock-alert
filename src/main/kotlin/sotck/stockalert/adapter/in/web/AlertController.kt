package sotck.stockalert.adapter.`in`.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import sotck.stockalert.application.service.AlertManagementService
import sotck.stockalert.application.service.CreateAlertRequest
import sotck.stockalert.common.auth.AuthUserId
import sotck.stockalert.common.response.ApiResponse
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/alerts")
class AlertController(private val alertManagementService: AlertManagementService) {

    @PostMapping
    fun createAlert(@RequestBody request: CreateAlertApiRequest, @AuthUserId userId: Long): ApiResponse<AlertResponse> {
        val alert = alertManagementService.createAlert(
            CreateAlertRequest(
                userId = userId,
                stockCode = request.stockCode,
                alertType = request.alertType,
                targetPrice = request.targetPrice,
                changeRateThreshold = request.changeRateThreshold,
                isAbove = request.isAbove
            )
        )
        return ApiResponse.success(alert.toResponse())
    }

    @GetMapping
    fun getUserAlerts(@AuthUserId userId: Long): ApiResponse<List<AlertResponse>> {
        val alerts = alertManagementService.getUserAlerts(userId)
        return ApiResponse.success(alerts.map { it.toResponse() })
    }

    @DeleteMapping("/{alertId}")
    fun deleteAlert(
        @PathVariable alertId: Long,
        @AuthUserId userId: Long
    ) {
        alertManagementService.deleteAlert(alertId, userId)
    }

    @PutMapping("/{alertId}/disable")
    fun disableAlert(
        @PathVariable alertId: Long,
        @AuthUserId userId: Long
    ): ApiResponse<Unit> {
        alertManagementService.disableAlert(alertId, userId)
        return ApiResponse.success(Unit)
    }
}

data class CreateAlertApiRequest(
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal? = null,
    val changeRateThreshold: BigDecimal? = null,
    val isAbove: Boolean? = null
)

data class AlertResponse(
    val id: Long?,
    val stockCode: String,
    val stockName: String,
    val alertType: AlertType,
    val status: String,
    val targetPrice: BigDecimal?,
    val changeRateThreshold: BigDecimal?,
    val createdAt: String,
    val triggeredAt: String?
)

fun Alert.toResponse() = AlertResponse(
    id = this.id,
    stockCode = this.stock.stockCode,
    stockName = this.stock.stockName,
    alertType = this.alertType,
    status = this.status.name,
    targetPrice = this.condition.targetPrice,
    changeRateThreshold = this.condition.changeRateThreshold,
    createdAt = this.createdAt.toString(),
    triggeredAt = this.triggeredAt?.toString()
)