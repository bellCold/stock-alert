package sotck.stockalert.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import sotck.stockalert.common.auth.AuthUserIdArgumentResolver
import sotck.stockalert.common.ratelimit.RateLimitInterceptor

@Configuration
class WebMvcConfig(
    private val authUserIdArgumentResolver: AuthUserIdArgumentResolver,
    private val rateLimitInterceptor: RateLimitInterceptor
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authUserIdArgumentResolver)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/api/**")
    }
}
