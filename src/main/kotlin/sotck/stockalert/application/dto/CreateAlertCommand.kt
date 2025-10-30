package sotck.stockalert.application.dto

import sotck.stockalert.adapter.`in`.web.request.CreateAlertRequest
import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal

data class CreateAlertCommand(
    val userId: Long,
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal? = null,
    val changeRateThreshold: BigDecimal? = null,
    val isAbove: Boolean? = null
) {
    companion object {
        fun from(request: CreateAlertRequest, userId: Long): CreateAlertCommand {
            return CreateAlertCommand(
                userId = userId,
                stockCode = request.stockCode,
                alertType = request.alertType,
                targetPrice = request.targetPrice,
                changeRateThreshold = request.changeRateThreshold,
                isAbove = request.isAbove
            )
        }
    }
}