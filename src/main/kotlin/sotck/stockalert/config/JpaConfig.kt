package sotck.stockalert.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["sotck.stockalert.adapter.out.persistence"])
class JpaConfig
