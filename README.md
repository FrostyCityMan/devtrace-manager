# DevTrace Manager

DevTrace Manager는 SI 개발 프로젝트에서 발생하는 프로젝트 관리, 이슈 관리, 작업 공수 관리, 형상관리 로그 관리, 컬럼명세 Excel 생성, 개발 산출물 생성을 통합 지원하기 위한 스탠드얼론 웹 애플리케이션입니다.

현재 구축 범위는 Spring Boot 기본 골격, 프로젝트 관리 기능, 이슈 관리 CRUD 기능, 작업 공수 관리 기능, 컬럼명세 Excel 생성 기능, Git/SVN 변경이력 Excel 생성 기능입니다.

## 기술 스택

- Java 21
- Spring Boot
- Gradle
- Spring Security Form Login
- MyBatis
- PostgreSQL
- Flyway
- springdoc-openapi
- Apache POI
- Thymeleaf
- jQuery
- Bootstrap

## 실행 준비

PostgreSQL 데이터베이스를 준비하고 환경 변수를 설정합니다.

```powershell
$env:DB_URL='jdbc:postgresql://localhost:5432/devtrace_manager'
$env:DB_USERNAME='devtrace'
$env:DB_PASSWORD='devtrace'
```

## 실행

```powershell
gradle bootRun
```

기본 접속 정보:

- URL: `http://localhost:8080`
- 사용자: `admin`
- 비밀번호: `admin`

## DB 마이그레이션

애플리케이션 시작 시 Flyway가 자동으로 `src/main/resources/db/migration`의 마이그레이션을 적용합니다.

수동 적용이 필요하면 Gradle Flyway 플러그인을 별도로 추가하지 않았으므로 애플리케이션을 실행해 적용하십시오.

## 테스트

```powershell
gradle test
```

## 현재 구현 기능

- Spring Boot 기본 구조
- PostgreSQL, MyBatis, Flyway 설정
- Spring Security Form Login
- springdoc-openapi 설정
- 공통 API 응답 객체
- 공통 예외 처리
- PROJECT 테이블 Flyway 마이그레이션
- 프로젝트 목록, 상세, 등록, 수정, 삭제
- 프로젝트 관리 Thymeleaf 화면
- ProjectService 기본 단위 테스트
- ISSUE 테이블 Flyway 마이그레이션
- 이슈 목록, 상세, 등록, 수정, 삭제
- 프로젝트별 이슈 목록, 상태/우선순위/완료 예정일 필터
- 이슈 상태 변경 화면 및 API
- 이슈 목록 통계 카드와 공수 요약 위젯
- 이슈 관리 Thymeleaf 화면
- IssueService 기본 단위 테스트
- APP_USER 최소 테이블 및 기본 admin seed
- WORK_LOG 테이블 Flyway 마이그레이션
- 이슈별 작업 공수 등록, 수정, 삭제, 조회
- 작업 공수 합계의 ISSUE.SPENT_MINUTES 반영
- 이슈 상세 화면 작업 공수 영역
- WorkLogService 기본 단위 테스트
- COLUMN_SPEC 테이블 Flyway 마이그레이션
- PostgreSQL, Oracle, MySQL 기본 CREATE TABLE DDL Parser
- 컬럼명세 미리보기 화면
- 컬럼명세 요약 카드 및 DDL 분석 오류 화면 표시
- Apache POI 기반 컬럼명세 `.xlsx` 다운로드
- DDL 파싱 테스트 및 Excel 주요 셀 검증 테스트
- VCS_CHANGE_LOG, VCS_CHANGE_FILE, ISSUE_CHANGE_LOG_MAP 테이블 Flyway 마이그레이션
- Git 로그 텍스트 파서
- SVN XML 로그 파서
- 이슈 키 자동 매칭
- 변경이력 미리보기 화면
- Apache POI 기반 변경이력 `.xlsx` 다운로드
- Git/SVN 파서 테스트 및 Excel 주요 셀 검증 테스트

## 아직 구현하지 않은 기능

- 칸반 보드
- 산출물 생성
- 외부 API 연동
- React 전환
- JPA 전환
- 결제 기능
- 메일/Slack 알림
- Gantt Chart
- Sprint 기능
