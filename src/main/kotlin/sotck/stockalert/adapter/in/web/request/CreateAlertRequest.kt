package sotck.stockalert.adapter.`in`.web.request

import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal

data class CreateAlertRequest(
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal? = null,
    val changeRateThreshold: BigDecimal? = null,
    val isAbove: Boolean? = null
)