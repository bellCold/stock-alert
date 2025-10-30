# 주식 가격 업데이트 성능 최적화 전략

## 문제 상황
- 코스피 종목 수: 약 900개
- 현재 방식: 순차 처리
- 예상 소요 시간: API 호출 1개당 100ms 가정 시 90초

## 최적화 전략

### 1. 병렬 처리 (Kotlin Coroutines) ⭐ 추천
```kotlin
// 100개씩 청크로 나누어 병렬 처리
stocks.chunked(100).forEach { chunk ->
    val prices = async(Dispatchers.IO) {
        stockDataPort.getCurrentPrices(chunk.map { it.stockCode })
    }.await()

    // 가격 업데이트 및 저장
}
```

**장점:**
- 코루틴으로 경량 스레드 사용
- API 호출 병렬화로 10배 이상 속도 향상
- 청크 단위로 메모리 효율적 처리

**예상 성능:**
- 순차 처리: 90초
- 병렬 처리(10 concurrent): 9초
- 병렬 처리(100 concurrent): 1초 미만

---

### 2. 우선순위 기반 업데이트
```kotlin
// 1. 알림이 설정된 종목만 업데이트
val stocksWithAlerts = alertRepository.findActiveAlerts()
    .map { it.stock.stockCode }
    .distinct()

// 2. 나머지는 낮은 우선순위로 처리
```

**효과:**
- 실제 사용 중인 종목만 실시간 업데이트
- 나머지는 캐시 활용 또는 느리게 업데이트

---

### 3. API 호출 최적화

#### 3-1. 배치 API 사용
```kotlin
// 한 번에 여러 종목 조회
GET /api/stocks/prices?codes=005930,000660,035720,...
```

#### 3-2. WebSocket 실시간 스트리밍
```kotlin
// 한 번 연결로 모든 종목 실시간 수신
websocket.subscribe(stockCodes) { price ->
    updateStockPrice(price)
}
```

---

### 4. 캐싱 전략 (Redis)
```kotlin
@Cacheable(value = "stock-prices", key = "#stockCode")
fun getCurrentPrice(stockCode: String): Price? {
    return stockDataPort.getCurrentPrice(stockCode)
}
```

**캐시 TTL 설정:**
- 장 중: 1-5초
- 장 마감 후: 1시간
- 휴장일: 24시간

---

### 5. 데이터베이스 배치 처리
```kotlin
// JPA Batch Insert/Update 활성화
spring.jpa.properties.hibernate.jdbc.batch_size=100
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

// 코드에서
@Transactional
fun updateAllStockPrices() {
    stocks.chunked(100).forEach { chunk ->
        stockRepository.saveAll(chunk)  // 배치 저장
    }
}
```

---

### 6. 증분 업데이트 (Incremental Update)
```kotlin
// 변경된 종목만 업데이트
val changedStocks = stocks.filter { stock ->
    val newPrice = prices[stock.stockCode]
    newPrice != null && newPrice != stock.currentPrice
}

stockRepository.saveAll(changedStocks)  // 변경된 것만 저장
```

---

### 7. 비동기 이벤트 처리
```kotlin
// 가격 업데이트는 빠르게, 부가 작업은 이벤트로
@Async
fun handlePriceChangeEvent(event: PriceChangeEvent) {
    // 알림 발송, 로그 기록 등
}
```

---

## 추천 구성

### Phase 1: 즉시 적용 가능
1. ✅ 배치 API 사용 (`getCurrentPrices()`)
2. ✅ JPA Batch 설정
3. ✅ 증분 업데이트

### Phase 2: 중기 개선
1. ✅ 코루틴 병렬 처리
2. ✅ Redis 캐싱
3. ✅ 우선순위 기반 업데이트

### Phase 3: 장기 개선
1. ⭐ WebSocket 실시간 스트리밍
2. ⭐ Kafka 이벤트 스트림
3. ⭐ 분산 처리 (여러 서버로 종목 분산)

---

## 성능 비교표

| 방식 | 900개 종목 처리 시간 | 장점 | 단점 |
|------|---------------------|------|------|
| 순차 처리 | 90초 | 단순 | 너무 느림 |
| 배치 API (100개씩) | 10초 | 구현 쉬움 | API 제한 |
| 코루틴 병렬 (100 concurrent) | 1초 | 빠름, 자원 효율적 | 코루틴 학습 필요 |
| Redis 캐싱 | 0.1초 (캐시 히트) | 매우 빠름 | 실시간성 감소 |
| WebSocket | 실시간 | 실시간, 효율적 | 구현 복잡 |

---

## 실전 적용 예시

### 현재 스케줄러
```kotlin
@Scheduled(fixedDelayString = "\${monitoring.stock.update-interval:60}000")
fun updateStockPrices() {
    // 60초마다 순차 처리 (90초 걸림 - 문제!)
}
```

### 개선된 스케줄러
```kotlin
@Scheduled(fixedDelayString = "\${monitoring.stock.update-interval:5}000")
fun updateStockPrices() {
    runBlocking {
        // 5초마다 병렬 처리 (1초 걸림)
        stockPriceMonitoringServiceAsync.updateAllStockPricesParallel()
    }
}
```

---

## 모니터링 지표

```kotlin
@Timed("stock.update.duration")
fun updateAllStockPrices() {
    val startTime = System.currentTimeMillis()

    // 업데이트 로직

    val duration = System.currentTimeMillis() - startTime
    log.info("Updated ${stocks.size} stocks in ${duration}ms")
}
```

**추적해야 할 메트릭:**
- 평균 처리 시간
- API 실패율
- 가격 변동 빈도
- 알림 발송 지연 시간
