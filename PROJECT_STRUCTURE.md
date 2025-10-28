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
    📄 Stock.kt              ← 주식 엔티티 (가격 업데이트, 이벤트 발생, 급등/급락 감지)
    📄 Price.kt              ← 가격 값 객체 (불변, 검증 로직, 0/음수 방어)
    📄 PriceChangeEvent.kt   ← 도메인 이벤트 (신고가, 급등, 급락)
    📄 StockRepository.kt    ← 인터페이스 (구현 없음!)
  📁 alert/
    📄 Alert.kt              ← 알림 엔티티 (조건 체크)
    📄 AlertCondition.kt     ← 알림 조건 로직
    📄 AlertType.kt          ← 알림 타입 정의
    📄 AlertStatus.kt        ← 알림 상태 (ACTIVE, INACTIVE, TRIGGERED)
    📄 AlertRepository.kt    ← 인터페이스
  📁 user/
    📄 User.kt               ← 사용자 엔티티 (ACTIVE/INACTIVE)
    📄 UserStatus.kt         ← 사용자 상태
    📄 UserRepository.kt     ← 인터페이스

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
      📄 AlertController.kt     ← REST API (POST, GET, DELETE, PUT /api/v1/alerts)
      📄 StockController.kt     ← REST API (GET, POST /api/v1/stocks)
    📁 scheduler/
      📄 StockMonitoringScheduler.kt  ← 주기적 작업 (60초마다 가격 업데이트)
  📁 out/ (아웃바운드)
    📁 persistence/
      📄 StockJpaRepository.kt        ← JPA 인터페이스
      📄 StockRepositoryAdapter.kt    ← Repository 구현
      📄 AlertJpaRepository.kt
      📄 AlertRepositoryAdapter.kt
      📄 UserJpaRepository.kt
      📄 UserRepositoryAdapter.kt
    📁 api/
      📄 NaverApiClient.kt            ← Naver Finance API (WebClient 기반)
    📁 notification/
      📄 LogNotificationAdapter.kt    ← 알림 전송 구현

📁 config/
  📄 JpaAuditingConfig.kt      ← JPA Auditing 설정 (BaseEntity)
  📄 RestTemplateConfig.kt
  📄 SchedulingConfig.kt
  📄 WebMvcConfig.kt           ← Interceptor 및 ArgumentResolver 등록
  📄 WebClientConfig.kt        ← WebClient 설정 (Naver API)
  📄 RedisConfig.kt            ← Redis 설정 (Rate Limiting)

📁 common/ (공통 기능)
  📁 auth/
    📄 AuthUserId.kt           ← 사용자 ID 어노테이션
    📄 AuthUserIdArgumentResolver.kt  ← 사용자 ID 자동 주입
  📁 ratelimit/
    📄 RateLimit.kt            ← Rate Limit 어노테이션
    📄 RateLimitInterceptor.kt ← Rate Limit 인터셉터 (Redis 기반)
  📁 logging/
    📄 LoggingFilter.kt        ← 요청/응답 로깅 필터
  📁 exception/
    📄 StockAlertException.kt  ← 공통 예외 클래스
    📄 ErrorCode.kt            ← 에러 코드 정의
    📄 GlobalExceptionHandler.kt  ← 전역 예외 처리
  📁 response/
    📄 ApiResponse.kt          ← 공통 API 응답 형식
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

### 2️⃣ 알림 생성 흐름 (Rate Limit 적용)
```
[클라이언트]
    ↓
[LoggingFilter] ← 요청/응답 로깅, MDC 설정
    ↓
[RateLimitInterceptor] ← Redis 기반 Rate Limit (3초)
    ↓
[AlertController] (POST /api/v1/alerts)
    ↓
[AuthUserIdArgumentResolver] ← X-User-Id 헤더 자동 주입
    ↓
[AlertManagementService]
    ↓
[Alert 엔티티 생성] ← 도메인 로직
    ↓
[AlertRepository] → [MySQL]
    ↓
[ApiResponse] ← 공통 응답 형식
    ↓
[GlobalExceptionHandler] ← 예외 발생 시 처리
```

### 3️⃣ 알림 발송 흐름
```
[스케줄러] (60초마다)
    ↓
[StockPriceMonitoringService]
    ↓
[NaverApiClient] → [Naver Finance API] (가격 조회)
    ↓
[Stock.updatePrice()] ← 도메인 로직 (급등/급락 감지)
    ↓
[PriceChangeEvent 발생]
    ↓
[Alert.checkCondition()] ← 도메인 로직 (조건 체크)
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
class YahooFinanceAdapter(private val webClient: WebClient) : StockDataPort {
    override fun getCurrentPrice(stockCode: String): Price? {
        // Yahoo Finance API 호출
    }

    override fun getCurrentPrices(stockCodes: List<String>): Map<String, Price> {
        // 배치 조회
    }
}
```

**현재 구현**: `NaverApiClient` (Naver Finance API 사용)

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
# 1. Docker로 MySQL, Redis 시작
docker-compose up -d

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. API 테스트
# Health Check
curl http://localhost:8080/api/v1/ping

# 주식 조회
curl http://localhost:8080/api/v1/stocks

# 알림 생성 (Rate Limit 적용)
curl -X POST http://localhost:8080/api/v1/alerts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"stockCode":"005930","alertType":"TARGET_PRICE","targetPrice":70000}'
```

## 환경 변수 설정

```bash
# .env 파일 또는 환경 변수로 설정
export KIS_APP_KEY=your-app-key
export KIS_APP_SECRET=your-app-secret
```

## 개발 현황

### 완료된 기능
1. ✅ 도메인 모델 설계 (Stock, Alert, User)
2. ✅ 헥사고날 아키텍처 구조
3. ✅ REST API 엔드포인트
4. ✅ Naver Finance API 연동 (WebClient)
5. ✅ 공통 예외 처리 (GlobalExceptionHandler, RFC 7807)
6. ✅ 공통 API 응답 형식 (ApiResponse)
7. ✅ 로깅 필터 (LoggingFilter, MDC)
8. ✅ 사용자 인증 (AuthUserId, ArgumentResolver)
9. ✅ Rate Limiting (Redis 기반, 다중 서버 지원)
10. ✅ 안전한 가격 계산 (0/음수 방어 로직)
11. ✅ 스케줄러 (주기적 가격 업데이트)

### 향후 계획
1. ⏳ 단위/통합 테스트 작성
2. ⏳ JWT 기반 인증/인가 구현
3. ⏳ WebSocket 실시간 알림
4. ⏳ Redis 캐싱 (주가 데이터)
5. ⏳ 성능 최적화 (코루틴 병렬 처리)
6. ⏳ 이벤트 소싱 (가격 이력 저장)

## 주요 기능

### ✅ Rate Limiting (따닥 방지)
- Redis 기반 분산 환경 지원
- POST, PUT, PATCH, DELETE 자동 적용
- 기본 3초 제한
- 사용자별 + URI별 독립적 제한
- 키 형식: `rate_limit:{userId}:{method}:{uri}`

### ✅ 로깅
- 요청/응답 자동 로깅
- MDC를 통한 Correlation ID 추적
- 사용자 ID 컨텍스트 전파
- 요청 시간 측정

### ✅ 예외 처리
- 전역 예외 핸들러
- RFC 7807 Problem Details 형식
- 에러 코드 체계화
- 로그 레벨 자동 분류