package sotck.stockalert.application.dto

import sotck.stockalert.adapter.`in`.web.request.SignUpRequest

data class SignUpCommand(val email: String, val name: String, val password: String) {
    companion object {
        fun from(request: SignUpRequest): SignUpCommand {
            return SignUpCommand(
                email = request.email,
                name = request.name,
                password = request.password
            )
        }
    }
}