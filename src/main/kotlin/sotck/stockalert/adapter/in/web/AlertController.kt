package sotck.stockalert.adapter.`in`.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sotck.stockalert.application.service.AlertManagementService
import sotck.stockalert.application.service.CreateAlertRequest
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/alerts")
class AlertController(private val alertManagementService: AlertManagementService) {

    @PostMapping
    fun createAlert(@RequestBody request: CreateAlertApiRequest, @RequestHeader("X-User-Id") userId: Long): ResponseEntity<AlertResponse> {
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
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(alert.toResponse())
    }

    @GetMapping
    fun getUserAlerts(@RequestHeader("X-User-Id") userId: Long): ResponseEntity<List<AlertResponse>> {
        val alerts = alertManagementService.getUserAlerts(userId)
        return ResponseEntity.ok(alerts.map { it.toResponse() })
    }

    @DeleteMapping("/{alertId}")
    fun deleteAlert(@PathVariable alertId: Long, @RequestHeader("X-User-Id") userId: Long): ResponseEntity<Void> {
        alertManagementService.deleteAlert(alertId, userId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{alertId}/disable")
    fun disableAlert(@PathVariable alertId: Long, @RequestHeader("X-User-Id") userId: Long): ResponseEntity<Void> {
        alertManagementService.disableAlert(alertId, userId)
        return ResponseEntity.ok().build()
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