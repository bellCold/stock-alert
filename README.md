# 주식 알림 서비스 (Stock Alert Service)

주식 시장의 중요한 가격 변동을 실시간으로 모니터링하고 알림을 제공하는 서비스입니다.

## 주요 기능

### 가격 알림
- **신고가 알림**: 주식이 역대 최고가를 경신했을 때 알림
- **급등/급락 알림**: 특정 비율(%) 이상 상승 또는 하락 시 알림
- **목표가 알림**: 사용자가 설정한 목표 가격 도달 시 알림
- **변동률 알림**: 일정 변동률 이상의 가격 변화 감지

### 맞춤형 모니터링
- 관심 종목 등록 및 관리
- 개별 종목별 알림 조건 설정
- 실시간 가격 추적

## 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Java Version**: 21

## 프로젝트 구조

```
stock-alert/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── sotck/stockalert/
│   │   │       └── StockAlertApplication.kt
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── kotlin/
│           └── sotck/stockalert/
│               └── StockAlertApplicationTests.kt
├── build.gradle.kts
└── settings.gradle.kts
```

## 시작하기

### 필수 요구사항

- JDK 21 이상
- MySQL 데이터베이스
- Gradle

### 설치 및 실행

1. **프로젝트 클론**
   ```bash
   git clone <repository-url>
   cd stock-alert
   ```

2. **데이터베이스 설정**

   MySQL 데이터베이스를 생성하고 `src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 설정합니다.

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/stock_alert
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **프로젝트 빌드**
   ```bash
   ./gradlew build
   ```

4. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

   또는 빌드된 JAR 파일 실행:
   ```bash
   java -jar build/libs/stock-alert-0.0.1-SNAPSHOT.jar
   ```

## 사용 예시

### 알림 조건 설정 예시

- **신고가 알림**: 삼성전자가 역대 최고가를 경신하면 알림
- **급등 알림**: 네이버가 5% 이상 상승하면 알림
- **급락 알림**: 카카오가 3% 이상 하락하면 알림
- **목표가 알림**: SK하이닉스가 150,000원에 도달하면 알림

## 개발 계획

- [ ] 실시간 주가 데이터 수집 API 연동
- [ ] 사용자 인증 및 권한 관리
- [ ] 알림 전송 기능 (이메일, SMS, 푸시 알림)
- [ ] 대시보드 UI 개발
- [ ] 알림 이력 조회 기능
- [ ] 다양한 기술적 지표 기반 알림

## 기여하기

프로젝트 개선을 위한 기여를 환영합니다!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스

이 프로젝트는 학습 목적으로 개발되었습니다.

## 문의

프로젝트 관련 문의사항이나 버그 리포트는 이슈를 등록해 주세요.