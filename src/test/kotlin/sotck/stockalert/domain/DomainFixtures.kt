package sotck.stockalert.domain

import sotck.stockalert.domain.alert.Alert
import sotck.stockalert.domain.alert.AlertCondition
import sotck.stockalert.domain.alert.AlertType
import sotck.stockalert.domain.stock.Price
import sotck.stockalert.domain.stock.Stock
import java.math.BigDecimal

object StockFixture {
    fun create(
        stockCode: String = "005930",
        stockName: String = "삼성전자",
        currentPrice: String = "60000",
        highestPrice: String = currentPrice
    ): Stock {
        return Stock(
            stockCode = stockCode,
            stockName = stockName,
            currentPrice = Price(BigDecimal(currentPrice)),
            highestPrice = Price(BigDecimal(highestPrice))
        )
    }
}

object AlertConditionFixture {
    fun targetPriceAbove(targetPrice: String = "60000"): AlertCondition {
        return AlertCondition(targetPrice = BigDecimal(targetPrice), isAbove = true)
    }

    fun targetPriceBelow(targetPrice: String = "60000"): AlertCondition {
        return AlertCondition(targetPrice = BigDecimal(targetPrice), isAbove = false)
    }

    fun changeRate(threshold: String = "5.0"): AlertCondition {
        return AlertCondition(changeRateThreshold = BigDecimal(threshold))
    }

    fun empty(): AlertCondition {
        return AlertCondition()
    }
}

object AlertFixture {
    fun create(
        stock: Stock = StockFixture.create(),
        userId: Long = 1L,
        alertType: AlertType = AlertType.TARGET_PRICE,
        condition: AlertCondition = AlertConditionFixture.targetPriceAbove()
    ): Alert {
        return Alert(
            stock = stock,
            userId = userId,
            alertType = alertType,
            condition = condition
        )
    }
}
