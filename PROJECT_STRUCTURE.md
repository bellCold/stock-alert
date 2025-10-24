# 프로젝트 구조 한눈에 보기

## 최신 트렌드 아키텍처 적용

### ✅ 헥사고날 아키텍처 (Ports & Adapters)
- 비즈니스 로직과 기술 스택 완전 분리
- 테스트 용이성 극대화
- 기술 스택 교체 자유로움

### ✅ 도메인 주도 설계 (DDD)
- 도메인 중심 설계
- 값 객체(Value Object)로 도메인 개념 명확화
- 풍부한 도메인 모델(Rich Domain Model)

### ✅ SOLID 원칙
- 단일 책임 원칙: 각 클래스는 하나의 책임만
- 개방-폐쇄 원칙: 확장에는 열려있고 수정에는 닫혀있음
- 의존성 역전 원칙: Port 인터페이스를 통한 추상화

## 핵심 파일 구조

```
📁 domain/ (도메인 계층 - 비즈니스 핵심)
  📁 stock/
    📄 Stock.kt              ← 주식 엔티티 (가격 업데이트, 이벤트 발생)
    📄 Price.kt              ← 가격 값 객체 (불변, 검증 로직)
    📄 PriceChangeEvent.kt   ← 도메인 이벤트 (신고가, 급등, 급락)
    📄 StockRepository.kt    ← 인터페이스 (구현 없음!)
  📁 alert/
    📄 Alert.kt              ← 알림 엔티티 (조건 체크)
    📄 AlertCondition.kt     ← 알림 조건 로직
    📄 AlertType.kt          ← 알림 타입 정의
    📄 AlertRepository.kt    ← 인터페이스

📁 application/ (애플리케이션 계층 - Use Case)
  📁 service/
    📄 StockPriceMonitoringService.kt  ← 주가 모니터링 유즈케이스
    📄 AlertManagementService.kt       ← 알림 관리 유즈케이스
  📁 port/out/
    📄 StockDataPort.kt      ← 외부 API 인터페이스
    📄 NotificationPort.kt   ← 알림 전송 인터페이스

📁 adapter/ (어댑터 계층 - 외부 세계 연결)
  📁 in/ (인바운드)
    📁 web/
      📄 AlertController.kt     ← REST API
      📄 StockController.kt
    📁 scheduler/
      📄 StockMonitoringScheduler.kt  ← 주기적 작업
  📁 out/ (아웃바운드)
    📁 persistence/
      📄 StockJpaRepository.kt        ← JPA 인터페이스
      📄 StockRepositoryAdapter.kt    ← Repository 구현
      📄 AlertJpaRepository.kt
      📄 AlertRepositoryAdapter.kt
    📁 api/
      📄 KisApiClient.kt              ← 한국투자증권 API
    📁 notification/
      📄 LogNotificationAdapter.kt    ← 알림 전송 구현

📁 config/
  📄 RestTemplateConfig.kt
  📄 SchedulingConfig.kt
```

## 데이터 흐름

### 1️⃣ 주식 가격 업데이트 흐름
```
[스케줄러]
    ↓
[StockPriceMonitoringService]
    ↓
[StockDataPort] → [KisApiClient] → [한국투자증권 API]
    ↓
[Stock.updatePrice()] ← 도메인 로직 실행
    ↓
[PriceChangeEvent 발생] (신고가? 급등? 급락?)
    ↓
[StockRepository] → [StockRepositoryAdapter] → [MySQL]
```

### 2️⃣ 알림 생성 흐름
```
[클라이언트]
    ↓
[AlertController] (POST /api/v1/alerts)
    ↓
[AlertManagementService]
    ↓
[Alert 엔티티 생성] ← 도메인 로직
    ↓
[AlertRepository] → [MySQL]
```

### 3️⃣ 알림 발송 흐름
```
[스케줄러]
    ↓
[StockPriceMonitoringService.checkAndNotifyAlerts()]
    ↓
[Alert.checkCondition()] ← 도메인 로직
    ↓ (조건 만족 시)
[NotificationPort] → [LogNotificationAdapter]
    ↓
[알림 전송] (로그/이메일/SMS/Push)
```

## 확장 포인트

### 🔧 새로운 API 추가 (예: Yahoo Finance)
```kotlin
// 1. Port는 이미 정의되어 있음 (StockDataPort)
// 2. 새 Adapter만 추가
@Component
class YahooFinanceAdapter : StockDataPort {
    override fun getCurrentPrice(stockCode: String): Price? {
        // Yahoo Finance API 호출
    }
}
```

### 🔧 새로운 알림 타입 추가
```kotlin
// 1. AlertType enum 추가
enum class AlertType {
    NEW_HIGH_PRICE,
    SURGE,
    FALL,
    TARGET_PRICE,
    CHANGE_RATE,
    VOLUME_SPIKE    // ← 새로운 타입!
}

// 2. AlertCondition 로직 추가
// 3. 기존 코드는 수정 없음!
```

### 🔧 새로운 알림 채널 추가 (예: 카카오톡)
```kotlin
@Component
class KakaoNotificationAdapter : NotificationPort {
    override fun send(alert: Alert, message: String) {
        // 카카오톡 API 호출
    }
}
```

## 왜 이 구조가 좋은가?

### ✨ 확장성
- 새로운 기능 추가 시 기존 코드 수정 최소화
- 인터페이스 기반 설계로 구현체 교체 용이

### ✨ 유연성
- 비즈니스 로직(Domain)과 기술(Adapter) 분리
- 데이터베이스, API, 알림 방식 자유롭게 변경 가능

### ✨ 수정 용이성
- 명확한 계층 구조로 코드 위치 파악 쉬움
- 단일 책임 원칙으로 수정 범위 명확

### ✨ 테스트 가능성
- Mock 객체로 Port 대체하여 단위 테스트
- 도메인 로직은 외부 의존성 없이 테스트

## 주요 설계 결정

| 항목 | 선택 | 이유 |
|-----|-----|-----|
| 아키텍처 | Hexagonal | 비즈니스 로직 보호, 테스트 용이성 |
| 도메인 모델 | Rich Domain Model | 로직을 도메인에 집중 |
| 값 객체 | Price | 가격 관련 로직 캡슐화, 불변성 |
| 이벤트 | Sealed Class | 타입 안전성, 패턴 매칭 |
| 의존성 방향 | 단방향 (외부→내부) | Domain은 순수하게 유지 |
| Repository | Interface 분리 | JPA 의존성을 Domain에서 제거 |

## 실행 방법

```bash
# 1. Docker로 MySQL 시작
docker-compose up -d

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. API 테스트
curl http://localhost:8080/api/v1/stocks
```

## 환경 변수 설정

```bash
# .env 파일 또는 환경 변수로 설정
export KIS_APP_KEY=your-app-key
export KIS_APP_SECRET=your-app-secret
```

## 다음 단계

1. ✅ 도메인 모델 설계 완료
2. ✅ 헥사고날 아키텍처 구조 완료
3. ✅ REST API 엔드포인트 완료
4. ⏳ 실제 API 연동 테스트
5. ⏳ 단위/통합 테스트 작성
6. ⏳ 인증/인가 구현
7. ⏳ WebSocket 실시간 알림
8. ⏳ Redis 캐싱 추가