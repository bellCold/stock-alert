package sotck.stockalert

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockAlertApplication

fun main(args: Array<String>) {
    runApplication<StockAlertApplication>(*args)
}
