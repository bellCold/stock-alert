package sotck.stockalert.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import sotck.stockalert.domain.stock.Stock

interface StockJpaRepository : JpaRepository<Stock, Long> {
    fun findByStockCode(stockCode: String): Stock?
}