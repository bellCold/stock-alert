package sotck.stockalert.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import sotck.stockalert.common.auth.AuthUserIdArgumentResolver

@Configuration
class WebMvcConfig(
    private val authUserIdArgumentResolver: AuthUserIdArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authUserIdArgumentResolver)
    }
}
