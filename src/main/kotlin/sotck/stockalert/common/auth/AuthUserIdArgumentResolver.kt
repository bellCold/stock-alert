package sotck.stockalert.common.auth

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthUserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthUserId::class.java) && parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {
        val userId = webRequest.getHeader(USER_ID_HEADER) ?: throw IllegalArgumentException("X-User-Id header is required")

        return userId.toLongOrNull() ?: throw IllegalArgumentException("X-User-Id header must be a valid number")
    }

    companion object {
        private const val USER_ID_HEADER = "X-User-Id"
    }
}
