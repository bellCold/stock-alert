package sotck.stockalert.application.dto

import sotck.stockalert.adapter.`in`.web.request.SignInRequest

data class SignInCommand(val email: String, val password: String) {
    companion object {
        fun from(request: SignInRequest): SignInCommand {
            return SignInCommand(
                email = request.email,
                password = request.password
            )
        }
    }
}