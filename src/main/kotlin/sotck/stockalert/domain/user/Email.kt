package sotck.stockalert.domain.user

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "이메일은 빈 값일 수 없습니다." }
        require(value.matches(EMAIL_REGEX)) { "유효하지 않은 이메일 형식입니다: $value" }
        require(value.length <= MAX_LENGTH) { "이메일은 최대 ${MAX_LENGTH}자까지 입력 가능합니다." }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        private const val MAX_LENGTH = 255
    }
}
