package sotck.stockalert.adapter.`in`.web

import org.springframework.web.bind.annotation.*
import sotck.stockalert.adapter.`in`.web.response.StockResponse
import sotck.stockalert.application.port.`in`.GetAllStocksUseCase
import sotck.stockalert.application.port.`in`.GetStockUseCase
import sotck.stockalert.application.port.`in`.UpdateStockPriceUseCase
import sotck.stockalert.common.response.ApiResponse

@RestController
@RequestMapping("/api/v1/stocks")
class StockController(
    private val getStockUseCase: GetStockUseCase,
    private val getAllStocksUseCase: GetAllStocksUseCase,
    private val updateStockPriceUseCase: UpdateStockPriceUseCase
) {

    @GetMapping("/{stockCode}")
    fun getStock(@PathVariable stockCode: String): ApiResponse<StockResponse> {
        val stock = getStockUseCase.getStock(stockCode)
        return ApiResponse.success(StockResponse.from(stock))
    }

    @PostMapping("/{stockCode}/refresh")
    fun refreshStockPrice(@PathVariable stockCode: String): ApiResponse<StockResponse> {
        updateStockPriceUseCase.updateStockPrice(stockCode)
        val stock = getStockUseCase.getStock(stockCode)
        return ApiResponse.success(StockResponse.from(stock))
    }

    @GetMapping
    fun getAllStocks(): ApiResponse<List<StockResponse>> {
        val stocks = getAllStocksUseCase.getAllStocks()
        return ApiResponse.success(stocks.map { StockResponse.from(it) })
    }
}