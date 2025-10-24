package sotck.stockalert.domain.alert

enum class AlertType(val description: String) {
    NEW_HIGH_PRICE("신고가"),
    SURGE("급등"),
    FALL("급락"),
    TARGET_PRICE("목표가"),
    CHANGE_RATE("변동률")
}