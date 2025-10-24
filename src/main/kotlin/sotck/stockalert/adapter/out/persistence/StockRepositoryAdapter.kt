package sotck.stockalert.adapter.out.persistence

import org.springframework.stereotype.Repository
import sotck.stockalert.domain.stock.Stock
import sotck.stockalert.domain.stock.StockRepository

@Repository
class StockRepositoryAdapter(private val stockJpaRepository: StockJpaRepository) : StockRepository {

    override fun findByStockCode(stockCode: String): Stock? {
        return stockJpaRepository.findByStockCode(stockCode)
    }

    override fun findAll(): List<Stock> {
        return stockJpaRepository.findAll()
    }

    override fun save(stock: Stock): Stock {
        return stockJpaRepository.save(stock)
    }

    override fun saveAll(stocks: List<Stock>): List<Stock> {
        return stockJpaRepository.saveAll(stocks)
    }
}