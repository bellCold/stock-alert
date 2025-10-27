package sotck.stockalert.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sotck.stockalert.common.exception.StockNotFoundException
import sotck.stockalert.domain.stock.Stock
import sotck.stockalert.domain.stock.StockRepository

@Service
@Transactional(readOnly = true)
class StockQueryService(private val stockRepository: StockRepository) {
    fun getStock(stockCode: String): Stock {
        return stockRepository.findByStockCode(stockCode) ?: throw StockNotFoundException(stockCode)
    }

    fun getAllStocks(): List<Stock> {
        return stockRepository.findAll()
    }
}
