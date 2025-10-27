package sotck.stockalert.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertStatus

interface AlertJpaRepository : JpaRepository<Alert, Long> {
    fun findByUserId(userId: Long): List<Alert>

    fun findByStockId(stockId: Long): List<Alert>

    fun findByStatus(status: AlertStatus): List<Alert>
}