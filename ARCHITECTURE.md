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
│   ├── stock/                        # 주식 도메인
│   │   ├── Stock.kt                  # 주식 엔티티
│   │   ├── Price.kt                  # 가격 값 객체
│   │   ├── PriceChangeEvent.kt       # 가격 변동 이벤트
│   │   └── StockRepository.kt        # 저장소 인터페이스
│   └── alert/                        # 알림 도메인
│       ├── Alert.kt                  # 알림 엔티티
│       ├── AlertType.kt              # 알림 타입
│       ├── AlertStatus.kt            # 알림 상태
│       ├── AlertCondition.kt         # 알림 조건
│       └── AlertRepository.kt        # 저장소 인터페이스
│
├── application/                      # 애플리케이션 서비스 계층
│   ├── service/                      # Use Case 구현
│   │   ├── StockPriceMonitoringService.kt
│   │   └── AlertManagementService.kt
│   └── port/                         # 포트 정의
│       └── out/                      # 아웃바운드 포트
│           ├── StockDataPort.kt      # 외부 주식 데이터 API 포트
│           └── NotificationPort.kt   # 알림 전송 포트
│
├── adapter/                          # 어댑터 구현
│   ├── in/                           # 인바운드 어댑터
│   │   ├── web/                      # REST API
│   │   │   ├── AlertController.kt
│   │   │   └── StockController.kt
│   │   └── scheduler/                # 스케줄러
│   │       └── StockMonitoringScheduler.kt
│   └── out/                          # 아웃바운드 어댑터
│       ├── persistence/              # 데이터베이스
│       │   ├── StockJpaRepository.kt
│       │   ├── StockRepositoryAdapter.kt
│       │   ├── AlertJpaRepository.kt
│       │   └── AlertRepositoryAdapter.kt
│       ├── api/                      # 외부 API 클라이언트
│       │   └── KisApiClient.kt       # 한국투자증권 API
│       └── notification/             # 알림 전송
│           └── LogNotificationAdapter.kt
│
└── config/                           # 설정
    ├── RestTemplateConfig.kt
    └── SchedulingConfig.kt
```

## 계층별 역할

### 1. Domain Layer (도메인 계층)
- **역할**: 핵심 비즈니스 로직과 규칙을 담당
- **특징**:
  - 외부 의존성 없음 (순수 Kotlin/Java)
  - 불변성(Immutability) 중시
  - 값 객체(Value Object)로 도메인 개념 표현
- **주요 클래스**:
  - `Stock`: 주식 엔티티, 가격 업데이트 및 이벤트 발생
  - `Price`: 가격 값 객체, 가격 비교 로직 포함
  - `Alert`: 알림 엔티티, 조건 체크 로직 포함
  - `AlertCondition`: 알림 조건 값 객체

### 2. Application Layer (애플리케이션 계층)
- **역할**: Use Case 구현, 도메인 객체들의 조합
- **특징**:
  - 트랜잭션 관리
  - 도메인 로직 오케스트레이션
  - Port 인터페이스 정의
- **주요 서비스**:
  - `StockPriceMonitoringService`: 주가 모니터링 및 알림 처리
  - `AlertManagementService`: 알림 CRUD 관리

### 3. Adapter Layer (어댑터 계층)
- **역할**: 외부 세계와의 연결
- **특징**:
  - Port 인터페이스 구현
  - 기술 스택 의존성 격리
  - 쉽게 교체 가능

#### Inbound Adapters (인바운드 어댑터)
- **REST API**: 클라이언트 요청 처리
- **Scheduler**: 주기적 작업 실행

#### Outbound Adapters (아웃바운드 어댑터)
- **Persistence**: 데이터베이스 접근
- **API Client**: 외부 주식 데이터 API 호출
- **Notification**: 알림 전송 (이메일, SMS, Push 등)

## 주요 디자인 패턴

### 1. Hexagonal Architecture (포트 & 어댑터)
```
[외부 세계] ←→ [Adapter] ←→ [Port] ←→ [Application] ←→ [Domain]
```

**장점**:
- 비즈니스 로직과 기술 스택 분리
- 테스트 용이성 (Mock 객체로 Port 대체)
- 기술 스택 교체 용이 (예: MySQL → PostgreSQL)

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

## 확장 시나리오

### 1. 새로운 알림 타입 추가
1. `AlertType` enum에 타입 추가
2. `AlertCondition`에 조건 로직 추가
3. 기존 코드 수정 최소화

### 2. 새로운 주식 API 추가
1. `StockDataPort` 인터페이스 구현
2. 새로운 Adapter 클래스 작성
3. 설정 파일에서 전환 가능

```kotlin
@Component
class YahooFinanceAdapter : StockDataPort {
    override fun getCurrentPrice(stockCode: String): Price? {
        // Yahoo Finance API 호출
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
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle
- **Java Version**: 21

## API 엔드포인트

### 알림 관리
- `POST /api/v1/alerts` - 알림 생성
- `GET /api/v1/alerts` - 사용자 알림 목록 조회
- `DELETE /api/v1/alerts/{alertId}` - 알림 삭제
- `PUT /api/v1/alerts/{alertId}/disable` - 알림 비활성화

### 주식 정보
- `GET /api/v1/stocks/{stockCode}` - 주식 정보 조회
- `POST /api/v1/stocks/{stockCode}/refresh` - 주식 가격 갱신
- `GET /api/v1/stocks` - 전체 주식 목록 조회

## 배경 작업 (Scheduler)

- **주식 가격 업데이트**: 60초마다 실행 (설정 가능)
- **알림 조건 체크**: 30초마다 실행 (설정 가능)

## 향후 개선 사항

1. **이벤트 소싱(Event Sourcing)**: 모든 가격 변동 이력 저장
2. **CQRS**: 읽기/쓰기 모델 분리
3. **Redis 캐싱**: 실시간 가격 데이터 캐싱
4. **Kafka**: 대용량 이벤트 스트리밍
5. **WebSocket**: 실시간 알림 푸시
6. **멀티 모듈**: 도메인별 모듈 분리
7. **테스트 자동화**: 단위/통합 테스트 작성