package sotck.stockalert.domain.stock

interface StockRepository {
    fun findById(id: Long): Stock?
    fun findByStockCode(stockCode: String): Stock?
    fun findAll(): List<Stock>
    fun save(stock: Stock): Stock
    fun saveAll(stocks: List<Stock>): List<Stock>
}