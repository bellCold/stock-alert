# 주식 알림 서비스 아키텍처

## 아키텍처 개요

이 프로젝트는 **헥사고날 아키텍처(Hexagonal Architecture)** 와 **도메인 주도 설계(DDD)** 원칙을 기반으로 설계되었습니다.

### 핵심 설계 원칙

1. **확장성(Scalability)**: 새로운 알림 타입이나 외부 API 추가가 용이
2. **유연성(Flexibility)**: 비즈니스 로직 변경 시 인프라 계층 영향 최소화
3. **유지보수성(Maintainability)**: 명확한 계층 분리로 코드 이해 및 수정 용이
4. **테스트 가능성(Testability)**: Port/Adapter 패턴으로 Mock 테스트 용이

## 패키지 구조

```
stock-alert/
├── domain/                           # 핵심 비즈니스 로직
│   ├── BaseEntity.kt                 # JPA Auditing 기본 엔티티
│   ├── stock/                        # 주식 도메인
│   │   ├── Stock.kt                  # 주식 엔티티 (가격 업데이트, 이벤트 발생)
│   │   ├── Price.kt                  # 가격 값 객체 (0/음수 방어 로직)
│   │   ├── PriceChangeEvent.kt       # 가격 변동 이벤트 (신고가/급등/급락)
│   │   └── StockRepository.kt        # 저장소 인터페이스
│   ├── alert/                        # 알림 도메인
│   │   ├── Alert.kt                  # 알림 엔티티
│   │   ├── AlertType.kt              # 알림 타입 (NEW_HIGH_PRICE, SURGE, FALL, etc)
│   │   ├── AlertStatus.kt            # 알림 상태 (ACTIVE, INACTIVE, TRIGGERED)
│   │   ├── AlertCondition.kt         # 알림 조건 (목표가, 변동률)
│   │   └── AlertRepository.kt        # 저장소 인터페이스
│   └── user/                         # 사용자 도메인
│       ├── User.kt                   # 사용자 엔티티 (ACTIVE/INACTIVE)
│       └── UserRepository.kt         # 저장소 인터페이스
│
├── application/                      # 애플리케이션 서비스 계층
│   ├── service/                      # Use Case 구현체
│   │   ├── StockPriceMonitoringService.kt  # 주가 모니터링, 알림 체크
│   │   ├── StockQueryService.kt            # 주식 조회 서비스
│   │   ├── AlertManagementService.kt       # 알림 CRUD 관리
│   │   └── AuthService.kt                  # 인증/인가 서비스
│   ├── dto/                          # DTO (Command, Result)
│   │   ├── CreateAlertCommand.kt     # 알림 생성 Command
│   │   ├── SignUpCommand.kt          # 회원가입 Command
│   │   ├── SignInCommand.kt          # 로그인 Command
│   │   └── AuthResult.kt             # 인증 결과 Result
│   └── port/                         # 포트 정의 (인터페이스)
│       ├── in/                       # 인바운드 포트 (UseCases)
│       │   ├── AlertUseCase.kt       # 알림 UseCases (Create, Get, Disable, Delete)
│       │   ├── AuthUseCase.kt        # 인증 UseCases (SignUp, SignIn, Refresh)
│       │   └── StockUseCase.kt       # 주식 UseCases (Get, GetAll, UpdatePrice)
│       └── out/                      # 아웃바운드 포트
│           ├── StockDataPort.kt      # 외부 주식 데이터 API 포트
│           └── NotificationPort.kt   # 알림 전송 포트
│
├── adapter/                          # 어댑터 구현
│   ├── in/                           # 인바운드 어댑터
│   │   ├── web/                      # REST API
│   │   │   ├── AlertController.kt    # 알림 API (/api/v1/alerts)
│   │   │   └── StockController.kt    # 주식 API (/api/v1/stocks)
│   │   └── scheduler/                # 스케줄러
│   │       └── StockMonitoringScheduler.kt  # 주기적 가격 업데이트
│   └── out/                          # 아웃바운드 어댑터
│       ├── persistence/              # 데이터베이스
│       │   ├── StockJpaRepository.kt
│       │   ├── StockRepositoryAdapter.kt
│       │   ├── AlertJpaRepository.kt
│       │   ├── AlertRepositoryAdapter.kt
│       │   ├── UserJpaRepository.kt
│       │   └── UserRepositoryAdapter.kt
│       ├── api/                      # 외부 API 클라이언트
│       │   └── NaverApiClient.kt     # Naver Finance API (WebClient 기반)
│       ├── cache/                    # Redis Repository
│       │   ├── RefreshToken.kt       # Refresh Token 엔티티 (@RedisHash)
│       │   └── RefreshTokenRepository.kt  # Redis Repository
│       └── notification/             # 알림 전송
│           └── LogNotificationAdapter.kt
│
├── common/                           # 공통 유틸리티 및 인프라
│   ├── Logger.kt                     # 로거 확장 함수
│   ├── auth/                         # 인증/인가
│   │   ├── AuthUserId.kt             # 사용자 ID 어노테이션
│   │   └── AuthUserIdArgumentResolver.kt
│   ├── ratelimit/                    # Rate Limiting
│   │   ├── RateLimit.kt              # Rate Limit 어노테이션
│   │   └── RateLimitInterceptor.kt   # Redis 기반 따닥 방지
│   ├── logging/                      # 로깅
│   │   └── LoggingFilter.kt          # 요청/응답 로깅, MDC 설정
│   ├── response/                     # 공통 응답
│   │   └── ApiResponse.kt            # 공통 API 응답 형식
│   └── exception/                    # 예외 처리
│       ├── StockAlertException.kt    # 커스텀 예외 계층 (Sealed class)
│       ├── ErrorCode.kt              # 에러 코드 정의 (HTTP 상태, 로그 레벨)
│       └── GlobalExceptionHandler.kt # 전역 예외 핸들러 (RFC 7807)
│
└── config/                           # 설정
    ├── SecurityConfig.kt             # Spring Security + JWT 설정
    ├── JpaConfig.kt                  # JPA Repository 스캔 (persistence 패키지)
    ├── JpaAuditingConfig.kt          # JPA Auditing 설정
    ├── RedisConfig.kt                # Redis Repository 스캔 (cache 패키지)
    ├── WebMvcConfig.kt               # ArgumentResolver, Interceptor 등록
    ├── WebClientConfig.kt            # WebClient 설정 (Naver API)
    ├── RestTemplateConfig.kt
    └── SchedulingConfig.kt
```

## 계층별 역할

### 1. Domain Layer (도메인 계층)
- **역할**: 핵심 비즈니스 로직과 규칙을 담당
- **특징**:
  - 외부 의존성 최소화 (JPA 제외)
  - 불변성(Immutability) 중시
  - 값 객체(Value Object)로 도메인 개념 표현
  - JPA Auditing을 통한 자동 타임스탬프 관리
- **주요 클래스**:
  - `BaseEntity`: createdAt/updatedAt 자동 관리
  - `Stock`: 주식 엔티티, 가격 업데이트 및 이벤트 발생, 급등/급락 감지
  - `Price`: 가격 값 객체, 가격 비교 로직 포함, 0/음수 방어
  - `Alert`: 알림 엔티티, 조건 체크 로직 포함
  - `AlertCondition`: 알림 조건 값 객체
  - `User`: 사용자 엔티티, 활성/비활성 상태 관리

### 2. Application Layer (애플리케이션 계층)
- **역할**: Use Case 구현, 도메인 객체들의 조합
- **특징**:
  - 트랜잭션 관리
  - 도메인 로직 오케스트레이션
  - Port 인터페이스 정의
  - 커스텀 예외를 통한 도메인 에러 처리
- **주요 서비스**:
  - `StockPriceMonitoringService`: 주가 모니터링 및 알림 처리
  - `AlertManagementService`: 알림 CRUD 관리 (StockNotFoundException, UnauthorizedAlertAccessException 사용)

### 3. Adapter Layer (어댑터 계층)
- **역할**: 외부 세계와의 연결
- **특징**:
  - Port 인터페이스 구현
  - 기술 스택 의존성 격리
  - 쉽게 교체 가능

#### Inbound Adapters (인바운드 어댑터)
- **REST API**: 클라이언트 요청 처리
  - `@AuthUserId`: 커스텀 어노테이션으로 사용자 인증 간소화
  - `AuthUserIdArgumentResolver`: X-User-Id 헤더 자동 파싱
- **Scheduler**: 주기적 작업 실행 (Logger extension 함수 사용)

#### Outbound Adapters (아웃바운드 어댑터)
- **Persistence**: 데이터베이스 접근
- **API Client**: 외부 주식 데이터 API 호출
- **Notification**: 알림 전송 (이메일, SMS, Push 등)

### 4. Common Layer (공통 계층)
- **역할**: 횡단 관심사(Cross-cutting Concerns) 처리
- **주요 구성요소**:
  - **Logger**: Extension 함수를 통한 간편한 로거 생성
    ```kotlin
    private val log = logger()  // 현재 클래스 타입으로 자동 생성
    ```
  - **Authentication**: `@AuthUserId` 어노테이션과 ArgumentResolver
    - X-User-Id 헤더 자동 파싱 및 검증
    - InvalidUserIdException 발생
  - **Exception Handling**: 체계적인 예외 처리 시스템
    - `StockAlertException`: Sealed class 기반 도메인 예외
    - `ErrorCode`: 에러 코드 및 HTTP 상태, 로그 레벨 관리
    - `GlobalExceptionHandler`: Spring ProblemDetail (RFC 7807) 반환
    - 로그 레벨별 자동 로깅 (DEBUG, INFO, WARN, ERROR)

## 주요 디자인 패턴

### 1. Hexagonal Architecture (포트 & 어댑터) - 완전한 구현
```
[Controller] ←→ [Inbound Port(UseCase)] ←→ [Service] ←→ [Outbound Port] ←→ [Adapter]
```

**Inbound (Driving) - 외부에서 애플리케이션으로**:
```kotlin
// 1. Controller (Adapter)
class AlertController(
    private val createAlertUseCase: CreateAlertUseCase,  // Port에 의존!
    private val getUserAlertsUseCase: GetUserAlertsUseCase
) {
    fun createAlert(request: CreateAlertRequest, userId: Long): ApiResponse<AlertResponse> {
        val alert = createAlertUseCase.createAlert(CreateAlertCommand.from(request, userId))
        return ApiResponse.success(AlertResponse.from(alert))
    }
}

// 2. Port (Interface)
interface CreateAlertUseCase {
    fun createAlert(command: CreateAlertCommand): Alert
}

// 3. Service (구현체)
@Service
class AlertManagementService : CreateAlertUseCase {
    override fun createAlert(command: CreateAlertCommand): Alert {
        // 비즈니스 로직 실행
    }
}
```

**Outbound (Driven) - 애플리케이션에서 외부로**:
```kotlin
// 1. Port (Interface)
interface StockDataPort {
    fun getCurrentPrice(stockCode: String): Price?
}

// 2. Adapter (구현체)
@Component
class NaverApiClient : StockDataPort {
    override fun getCurrentPrice(stockCode: String): Price? {
        // Naver API 호출
    }
}
```

**장점**:
- **의존성 역전**: Controller가 구현체가 아닌 인터페이스에 의존
- **테스트 용이성**: UseCase를 Mock으로 쉽게 대체 가능
- **유연성**: Service 구현을 변경해도 Controller는 영향 없음
- **명확한 경계**: Adapter ↔ Port ↔ Application ↔ Domain 경계 명확

### 2. Strategy Pattern (전략 패턴)
`AlertCondition`을 통해 다양한 알림 조건을 유연하게 처리

```kotlin
// 새로운 알림 조건 추가 시
sealed class AlertCondition {
    // 기존 조건들...
    data class CustomCondition(...) : AlertCondition()
}
```

### 3. Domain Events (도메인 이벤트)
가격 변동 시 이벤트 발생으로 느슨한 결합

```kotlin
sealed class PriceChangeEvent {
    data class NewHighPrice(...)
    data class Surge(...)
    data class Fall(...)
}
```

### 4. Extension Function Pattern (확장 함수 패턴)
Kotlin의 확장 함수를 활용한 코드 간결화

```kotlin
// Logger 생성
inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
```

### 5. Custom Annotation + ArgumentResolver
Spring MVC의 ArgumentResolver를 활용한 횡단 관심사 처리

```kotlin
@AuthUserId userId: Long  // X-User-Id 헤더 자동 파싱
```

### 6. DTO Layer Separation (DTO 계층 분리)
API Request/Response DTO와 Service Layer Command/Result DTO 분리

```kotlin
// 1. API Request (adapter/in/web/request)
data class CreateAlertRequest(
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal?
)

// 2. Service Command (application/dto)
data class CreateAlertCommand(
    val userId: Long,
    val stockCode: String,
    val alertType: AlertType,
    val targetPrice: BigDecimal?
) {
    companion object {
        fun from(request: CreateAlertRequest, userId: Long): CreateAlertCommand {
            return CreateAlertCommand(
                userId = userId,
                stockCode = request.stockCode,
                alertType = request.alertType,
                targetPrice = request.targetPrice
            )
        }
    }
}

// 3. API Response (adapter/in/web/response)
data class AlertResponse(
    val id: Long?,
    val stockId: Long,
    val alertType: AlertType
) {
    companion object {
        fun from(alert: Alert): AlertResponse {
            return AlertResponse(
                id = alert.id,
                stockId = alert.stockId,
                alertType = alert.alertType
            )
        }
    }
}
```

**장점**:
- **명확한 계층 분리**: API 계층과 Service 계층의 DTO 독립성
- **변환 로직 캡슐화**: Companion object의 from() 메서드로 변환 로직 집중
- **전역 접근 방지**: 확장 함수 대신 companion object 사용으로 스코프 제한
- **의도 명확화**: `ClassName.from()` 패턴으로 변환 의도 명시

### 7. RFC 7807 Problem Details
표준 에러 응답 포맷 사용

```json
{
  "type": "https://api.stockalert.com/errors/STOCK-1001",
  "title": "Stock not found",
  "status": 404,
  "detail": "Stock not found: 005930",
  "instance": "/api/v1/alerts",
  "errorCode": "STOCK-1001"
}
```

## 확장 시나리오

### 1. 새로운 알림 타입 추가
1. `AlertType` enum에 타입 추가
2. `AlertCondition`에 조건 로직 추가
3. 기존 코드 수정 최소화

### 2. 새로운 주식 API 추가
1. `StockDataPort` 인터페이스 구현
2. 새로운 Adapter 클래스 작성
3. 설정 파일에서 전환 가능

**현재 구현**: `NaverApiClient` (Naver Finance API, WebClient 기반)

```kotlin
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

### 3. 새로운 알림 채널 추가
1. `NotificationPort` 인터페이스 구현
2. 이메일, SMS, Push 알림 등 추가

```kotlin
@Component
class EmailNotificationAdapter : NotificationPort {
    override fun send(alert: Alert, message: String) {
        // 이메일 전송 로직
    }
}
```

## 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Database**: MySQL 8.0
- **Cache**: Redis (Rate Limiting, JWT Refresh Token)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA + Spring Data Redis
- **Build Tool**: Gradle
- **Java Version**: 21
- **Async**: Kotlin Coroutines 1.8.0
- **HTTP Client**: Spring WebFlux WebClient

## API 엔드포인트

### 인증 (Public)
- `POST /api/v1/auth/signup` - 회원가입
- `POST /api/v1/auth/signin` - 로그인 (JWT 발급)
- `POST /api/v1/auth/refresh` - Access Token 갱신

### 알림 관리 (인증 필요)
- `POST /api/v1/alerts` - 알림 생성
- `GET /api/v1/alerts` - 사용자 알림 목록 조회
- `DELETE /api/v1/alerts/{alertId}` - 알림 삭제
- `PUT /api/v1/alerts/{alertId}/disable` - 알림 비활성화

### 주식 정보 (인증 필요)
- `GET /api/v1/stocks/{stockCode}` - 주식 정보 조회
- `POST /api/v1/stocks/{stockCode}/refresh` - 주식 가격 갱신
- `GET /api/v1/stocks` - 전체 주식 목록 조회

## 배경 작업 (Scheduler)

- **주식 가격 업데이트**: 60초마다 실행 (설정: `monitoring.stock.update-interval`)
- **알림 조건 체크**: 30초마다 실행 (설정: `monitoring.alert.check-interval`)
- **구현**: `StockMonitoringScheduler.kt`

## 코드 품질 개선 사항

### 1. Logger 패턴 개선
- **Before**: `LoggerFactory.getLogger(javaClass)` 반복
- **After**: `private val log = logger()` Extension 함수 사용
- **이점**: 코드 간결화, 재사용성 향상

### 2. BaseEntity 도입
- **Before**: 각 엔티티마다 createdAt, updatedAt 수동 관리
- **After**: BaseEntity 상속 + JPA Auditing 자동 관리
- **이점**: 코드 중복 제거, 자동 타임스탬프 관리

### 3. 커스텀 예외 시스템
- **Before**: `IllegalArgumentException` 사용
- **After**: 도메인 특화 예외 (StockNotFoundException 등)
- **이점**: 명확한 에러 의도, 로그 레벨 자동화, RFC 7807 표준 준수

### 4. ArgumentResolver 활용
- **Before**: `@RequestHeader("X-User-Id") userId: Long` 반복
- **After**: `@AuthUserId userId: Long`
- **이점**: 코드 간결화, 검증 로직 중앙화

## 최근 개선 사항

### 1. 안전한 가격 계산 로직 (Stock.kt:48-63)
```kotlin
private fun calculateChangeRate(oldPrice: Price, newPrice: Price): BigDecimal {
    // oldPrice가 0이거나 음수면 변동률 계산 불가
    if (oldPrice.value <= BigDecimal.ZERO) {
        return BigDecimal.ZERO
    }

    // newPrice가 0이거나 음수면 변동률 계산 불가
    if (newPrice.value <= BigDecimal.ZERO) {
        return BigDecimal.ZERO
    }

    val priceDiff = newPrice.value - oldPrice.value
    return priceDiff.divide(oldPrice.value, 10, RoundingMode.HALF_UP) * BigDecimal(100)
}
```
- 0 또는 음수 가격에 대한 방어 로직
- `compareTo` 대신 `<=` 연산자 사용 (간결성)
- `BigDecimal` 정확한 계산, `RoundingMode` 명시

### 2. Naver Finance API 연동 (NaverApiClient.kt)
- WebClient 기반 비동기 HTTP 호출
- 배치 조회 지원 (`getCurrentPrices`)
- null-safe 처리, `runCatching`으로 에러 핸들링

### 3. User 도메인 추가
- 사용자 활성/비활성 상태 관리
- Alert와 User 연동 (userId 필드)

## 최근 구현 사항

### 1. JWT 인증/인가 시스템
```kotlin
// SecurityConfig.kt
@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
```

**특징:**
- Stateless JWT 인증 (세션 없음)
- Access Token (1시간) + Refresh Token (7일)
- Refresh Token은 Redis에 저장 (TTL 자동 관리)

### 2. Redis Repository 활용
```kotlin
@RedisHash(value = "refreshToken")
data class RefreshToken(
    @Id val userId: Long,
    val token: String,
    @TimeToLive(unit = TimeUnit.SECONDS)
    val ttl: Long = 604800 // 7일
)

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, Long>
```

**Repository 분리:**
- JPA: `adapter.out.persistence` 패키지
- Redis: `adapter.out.cache` 패키지
- 명시적 스캔 범위 지정으로 충돌 방지

### 3. 로깅 패턴 개선
```yaml
# logback-spring.xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %X{correlationId:-} %X{userId:-} %-5level %logger{36} - %msg%n</pattern>
```

**개선 사항:**
- MDC 값이 없을 때 기본값 표시 안 함
- 요청이 있을 때만 correlationId, userId 출력
- 깔끔한 로그 출력

## 향후 개선 사항

1. **이벤트 소싱(Event Sourcing)**: 모든 가격 변동 이력 저장
2. **CQRS**: 읽기/쓰기 모델 분리
3. **Redis 캐싱**: 실시간 가격 데이터 캐싱
4. **Kafka**: 대용량 이벤트 스트리밍
5. **WebSocket**: 실시간 알림 푸시
6. **멀티 모듈**: 도메인별 모듈 분리
7. **테스트 자동화**: 단위/통합 테스트 작성
8. **OpenAPI/Swagger**: API 문서 자동화
9. **성능 최적화**: 코루틴 병렬 처리 ([PERFORMANCE_OPTIMIZATION.md](PERFORMANCE_OPTIMIZATION.md) 참고)