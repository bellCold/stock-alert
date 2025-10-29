package sotck.stockalert.domain.user

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "비밀번호는 빈 값일 수 없습니다." }
        require(value.length >= MIN_LENGTH) { "비밀번호는 최소 ${MIN_LENGTH}자 이상이어야 합니다." }
        require(value.length <= MAX_LENGTH) { "비밀번호는 최대 ${MAX_LENGTH}자까지 입력 가능합니다." }
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 100
    }
}
