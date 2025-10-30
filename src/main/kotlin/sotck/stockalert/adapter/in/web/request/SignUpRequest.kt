package sotck.stockalert.adapter.`in`.web.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이어야 합니다.")
    val email: String,

    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
    val password: String
)