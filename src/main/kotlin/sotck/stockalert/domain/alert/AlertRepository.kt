package sotck.stockalert.domain.alert

interface AlertRepository {
    fun findByUserId(userId: Long): List<Alert>
    fun findByStockId(stockId: Long): List<Alert>
    fun findActiveAlerts(): List<Alert>
    fun save(alert: Alert): Alert
    fun delete(alert: Alert)
}