# 주식 알림 서비스 (Stock Alert Service)

주식 시장의 중요한 가격 변동을 실시간으로 모니터링하고 알림을 제공하는 서비스입니다.

**헥사고날 아키텍처**와 **도메인 주도 설계(DDD)** 원칙을 적용한 학습 프로젝트입니다.

## 주요 기능

### 가격 알림
- **신고가 알림**: 주식이 역대 최고가를 경신했을 때 알림
- **급등/급락 알림**: 특정 비율(5% 이상 급등, 3% 이상 급락) 감지 시 알림
- **목표가 알림**: 사용자가 설정한 목표 가격 도달 시 알림
- **변동률 알림**: 일정 변동률 이상의 가격 변화 감지

### 맞춤형 모니터링
- 관심 종목 등록 및 관리 (User 도메인 연동)
- 개별 종목별 알림 조건 설정
- 스케줄러를 통한 주기적 가격 업데이트
- 실시간 가격 추적 (Naver Finance API 연동)

### 시스템 안정성
- **Rate Limiting**: Redis 기반 따닥 방지 (기본 3초 제한)
- **로깅**: 요청/응답 자동 로깅, MDC를 통한 Correlation ID 추적
- **예외 처리**: RFC 7807 Problem Details 표준 준수, 체계적인 에러 코드 관리

## 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Database**: MySQL 8.0
- **Cache**: Redis (Rate Limiting, JWT Refresh Token)
- **ORM**: Spring Data JPA
- **Security**: Spring Security + JWT
- **Java Version**: 21
- **Async**: Kotlin Coroutines 1.8.0
- **HTTP Client**: Spring WebFlux WebClient

## 프로젝트 구조 (헥사고날 아키텍처)

```
stock-alert/
├── domain/                          # 핵심 비즈니스 로직
│   ├── stock/                       # 주식 도메인
│   │   ├── Stock.kt                 # 주식 엔티티, 가격 업데이트/이벤트 발생
│   │   ├── Price.kt                 # 가격 값 객체 (0/음수 방어 로직)
│   │   └── PriceChangeEvent.kt      # 도메인 이벤트 (신고가/급등/급락)
│   ├── alert/                       # 알림 도메인
│   │   ├── Alert.kt                 # 알림 엔티티
│   │   ├── AlertCondition.kt        # 알림 조건 로직
│   │   └── AlertType.kt             # 알림 타입 정의
│   └── user/                        # 사용자 도메인
│       └── User.kt                  # 사용자 엔티티
│
├── application/                     # Use Case 구현
│   ├── service/
│   │   ├── StockPriceMonitoringService.kt  # 주가 모니터링
│   │   └── AlertManagementService.kt       # 알림 관리
│   └── port/out/                    # 아웃바운드 포트
│       ├── StockDataPort.kt         # 외부 API 인터페이스
│       └── NotificationPort.kt      # 알림 전송 인터페이스
│
├── adapter/                         # 어댑터 구현
│   ├── in/
│   │   ├── web/                     # REST API (AlertController, AuthController)
│   │   └── scheduler/               # 스케줄러
│   └── out/
│       ├── persistence/             # JPA Repository
│       ├── cache/                   # Redis Repository (RefreshToken)
│       ├── api/                     # 외부 API 클라이언트
│       │   └── NaverApiClient.kt    # Naver Finance API
│       └── notification/            # 알림 전송
│
├── common/                          # 공통 기능
│   ├── auth/                        # @AuthUserId 어노테이션
│   ├── ratelimit/                   # Redis Rate Limiting
│   ├── logging/                     # 로깅 필터, MDC
│   ├── exception/                   # 예외 처리, ErrorCode
│   └── response/                    # 공통 응답 형식
│
└── config/                          # 설정
    ├── SecurityConfig.kt            # Spring Security + JWT 설정
    ├── JpaConfig.kt                 # JPA Repository 스캔 설정
    ├── RedisConfig.kt               # Redis Repository 스캔 설정
    ├── WebClientConfig.kt           # WebClient 설정
    └── WebMvcConfig.kt              # ArgumentResolver 등록
```

**상세 구조는 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 참고**

## 시작하기

### 필수 요구사항

- JDK 21 이상
- MySQL 8.0
- Redis (Rate Limiting 용도)
- Gradle 8.x

### 설치 및 실행

1. **프로젝트 클론**
   ```bash
   git clone <repository-url>
   cd stock-alert
   ```

2. **데이터베이스 및 Redis 설정**

   MySQL과 Redis를 시작합니다:
   ```bash
   # Docker 사용 시
   docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=stock_alert mysql:8.0
   docker run -d -p 6379:6379 redis:7-alpine
   ```

   `src/main/resources/application.properties` 설정:
   ```properties
   # MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/stock_alert
   spring.datasource.username=root
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update

   # Redis
   spring.data.redis.host=localhost
   spring.data.redis.port=6379

   # 스케줄러 설정
   monitoring.stock.update-interval=60  # 60초마다 주가 업데이트
   monitoring.alert.check-interval=30   # 30초마다 알림 체크
   ```

3. **프로젝트 빌드**
   ```bash
   ./gradlew build
   ```

4. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

5. **API 테스트**
   ```bash
   # Health Check
   curl http://localhost:8080/api/v1/ping

   # 주식 목록 조회
   curl http://localhost:8080/api/v1/stocks

   # 알림 생성 (X-User-Id 헤더 필수)
   curl -X POST http://localhost:8080/api/v1/alerts \
     -H "Content-Type: application/json" \
     -H "X-User-Id: 1" \
     -d '{
       "stockCode": "005930",
       "alertType": "TARGET_PRICE",
       "targetPrice": 70000,
       "isAbove": true
     }'
   ```

## API 엔드포인트

### 인증
- `POST /api/v1/auth/signup` - 회원가입
- `POST /api/v1/auth/signin` - 로그인 (Access Token + Refresh Token 발급)
- `POST /api/v1/auth/refresh` - Access Token 갱신

### 알림 관리
- `POST /api/v1/alerts` - 알림 생성
- `GET /api/v1/alerts` - 사용자 알림 목록 조회
- `DELETE /api/v1/alerts/{alertId}` - 알림 삭제
- `PUT /api/v1/alerts/{alertId}/disable` - 알림 비활성화

### 주식 정보
- `GET /api/v1/stocks/{stockCode}` - 주식 정보 조회
- `POST /api/v1/stocks/{stockCode}/refresh` - 주식 가격 갱신
- `GET /api/v1/stocks` - 전체 주식 목록 조회

대부분의 API는 `Authorization: Bearer {token}` 헤더를 통한 JWT 인증이 필요합니다.

## 주요 설계 특징

### 1. 헥사고날 아키텍처 (Ports & Adapters)
- 비즈니스 로직(Domain)과 기술 스택(Adapter) 완전 분리
- 외부 API 교체 용이 (현재: Naver Finance → 향후: Yahoo Finance 등)
- 테스트 용이성 극대화

### 2. 도메인 주도 설계 (DDD)
- 값 객체(Value Object) 활용: `Price`, `AlertCondition`
- 도메인 이벤트: `PriceChangeEvent` (신고가/급등/급락)
- Rich Domain Model: 비즈니스 로직을 도메인 객체에 집중

### 3. 안전한 가격 계산
- 0 또는 음수 가격에 대한 방어 로직
- `BigDecimal`을 사용한 정확한 금액 계산
- `compareTo`를 사용한 안전한 비교

### 4. Redis 기반 Rate Limiting
- 따닥 방지 (기본 3초 제한)
- 분산 환경 지원 (다중 서버)
- 사용자별 + URI별 독립적 제한

### 5. 체계적인 예외 처리
- RFC 7807 Problem Details 표준 준수
- 도메인 특화 예외 (`StockNotFoundException`, `UnauthorizedAlertAccessException`)
- 에러 코드 및 로그 레벨 자동 관리

## 개발 현황

### 완료된 기능
- ✅ 헥사고날 아키텍처 구조 설계
- ✅ 도메인 모델 설계 (Stock, Alert, User)
- ✅ REST API 엔드포인트
- ✅ JWT 기반 인증/인가 (Spring Security)
- ✅ Redis Repository (Refresh Token 저장)
- ✅ Naver Finance API 연동 (WebClient)
- ✅ 스케줄러를 통한 주기적 가격 업데이트
- ✅ Redis 기반 Rate Limiting
- ✅ 로깅 필터 (MDC, Correlation ID, 조건부 출력)
- ✅ 전역 예외 처리 (GlobalExceptionHandler)
- ✅ Repository 스캔 범위 명시 (JPA/Redis 분리)

### 향후 개선 사항
- [ ] 알림 전송 기능 (이메일, SMS, Push)
- [ ] WebSocket 실시간 알림
- [ ] Redis 캐싱 (주가 데이터)
- [ ] 단위/통합 테스트 작성
- [ ] 성능 최적화 (코루틴 병렬 처리)
- [ ] 이벤트 소싱 (가격 이력 저장)
- [ ] Kafka 이벤트 스트리밍

## 참고 문서

- [아키텍처 상세 설명](ARCHITECTURE.md)
- [프로젝트 구조 가이드](PROJECT_STRUCTURE.md)
- [성능 최적화 전략](PERFORMANCE_OPTIMIZATION.md)

## 라이선스

이 프로젝트는 학습 목적으로 개발되었습니다.