package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.*
import sotck.stockalert.adapter.`in`.web.request.CreateAlertRequest
import sotck.stockalert.adapter.`in`.web.response.AlertResponse
import sotck.stockalert.application.dto.CreateAlertCommand
import sotck.stockalert.application.port.`in`.CreateAlertUseCase
import sotck.stockalert.application.port.`in`.DeleteAlertUseCase
import sotck.stockalert.application.port.`in`.DisableAlertUseCase
import sotck.stockalert.application.port.`in`.GetUserAlertsUseCase
import sotck.stockalert.common.auth.AuthUserId
import sotck.stockalert.common.response.ApiResponse

@RestController
@RequestMapping("/api/v1/alerts")
class AlertController(
    private val createAlertUseCase: CreateAlertUseCase,
    private val getUserAlertsUseCase: GetUserAlertsUseCase,
    private val disableAlertUseCase: DisableAlertUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase
) {

    @PostMapping
    fun createAlert(@RequestBody request: CreateAlertRequest, @AuthUserId userId: Long): ApiResponse<AlertResponse> {
        val alert = createAlertUseCase.createAlert(CreateAlertCommand.from(request, userId))
        return ApiResponse.success(AlertResponse.from(alert))
    }

    @GetMapping
    fun getUserAlerts(@AuthUserId userId: Long): ApiResponse<List<AlertResponse>> {
        val alerts = getUserAlertsUseCase.getUserAlerts(userId)
        return ApiResponse.success(alerts.map { AlertResponse.from(it) })
    }

    @DeleteMapping("/{alertId}")
    fun deleteAlert(@PathVariable alertId: Long, @AuthUserId userId: Long) {
        deleteAlertUseCase.deleteAlert(alertId, userId)
    }

    @PutMapping("/{alertId}/disable")
    fun disableAlert(@PathVariable alertId: Long, @AuthUserId userId: Long): ApiResponse<Unit> {
        disableAlertUseCase.disableAlert(alertId, userId)
        return ApiResponse.success(Unit)
    }
}