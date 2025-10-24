package sotck.stockalert.domain.alert

enum class AlertStatus(val description: String) {
    ACTIVE("활성화"),
    TRIGGERED("발동됨"),
    DISABLED("비활성화")
}