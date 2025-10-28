package sotck.stockalert.adapter.out.persistence

import org.springframework.stereotype.Repository
import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertRepository
import sotck.stockalert.domain.alert.AlertStatus

@Repository
class AlertRepositoryAdapter(private val alertJpaRepository: AlertJpaRepository) : AlertRepository {
    override fun findById(id: Long): Alert? {
        return alertJpaRepository.findById(id).orElse(null)
    }

    override fun findByUserId(userId: Long): List<Alert> {
        return alertJpaRepository.findByUserId(userId)
    }

    override fun findByStockId(stockId: Long): List<Alert> {
        return alertJpaRepository.findByStockId(stockId)
    }

    override fun findActiveAlerts(): List<Alert> {
        return alertJpaRepository.findByStatus(AlertStatus.ACTIVE)
    }

    override fun save(alert: Alert): Alert {
        return alertJpaRepository.save(alert)
    }

    override fun delete(alert: Alert) {
        alertJpaRepository.delete(alert)
    }
}