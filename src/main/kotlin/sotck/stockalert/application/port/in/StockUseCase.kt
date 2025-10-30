package sotck.stockalert.application.port.`in`

import sotck.stockalert.domain.stock.Stock

interface GetStockUseCase {
    fun getStock(stockCode: String): Stock
}

interface GetAllStocksUseCase {
    fun getAllStocks(): List<Stock>
}

interface UpdateStockPriceUseCase {
    fun updateStockPrice(stockCode: String)
}