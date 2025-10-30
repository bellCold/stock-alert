package sotck.stockalert.adapter.`in`.web.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import sotck.stockalert.domain.alert.AlertType
import java.math.BigDecimal

data class CreateAlertRequest(
    @field:NotBlank(message = "종목 코드는 필수입니다.")
    val stockCode: String,

    @field:NotNull(message = "알림 타입은 필수입니다.")
    val alertType: AlertType,

    @field:Positive(message = "목표가는 양수여야 합니다.")
    val targetPrice: BigDecimal? = null,

    @field:Positive(message = "변동률은 양수여야 합니다.")
    val changeRateThreshold: BigDecimal? = null,

    val isAbove: Boolean? = null
)