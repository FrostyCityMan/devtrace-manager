# DevTrace Manager

DevTrace Manager는 SI 개발 프로젝트에서 발생하는 프로젝트 관리, 이슈 관리, 작업 공수 관리, 형상관리 로그 관리, 컬럼명세 Excel 생성, 개발 산출물 생성을 통합 지원하기 위한 스탠드얼론 웹 애플리케이션입니다.

현재 구축 범위는 Spring Boot 기본 골격, 프로젝트 관리 기능, 이슈 관리 CRUD 기능, 작업 공수 관리 기능, 컬럼명세 Excel 생성 기능, Git/SVN 변경이력 Excel 생성 기능, 주간/일일 업무보고 Markdown 생성 기능, 테스트 증적 관리 기능, 테스트 결과 보고서 Markdown/Excel 생성 기능, WBS 기반 일정 관리 기능, 칸반 보드 기능, 통합 운영 대시보드, 백로그·스프린트 관리 기능, 스프린트 분석 리포트 기능, 스프린트 일자별 스냅샷 기반 Burndown 기능입니다.

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
.\gradlew.bat bootRun
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
.\gradlew.bat test
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
- ARTIFACT_HISTORY 테이블 Flyway 마이그레이션
- 프로젝트/기간 기준 이슈, 작업 공수, Git/SVN 변경이력 집계
- 주간 업무보고 Markdown 생성기
- 주간 업무보고 미리보기 화면
- Markdown 다운로드 및 산출물 생성 이력 저장
- WeeklyReportMarkdownGenerator 및 ArtifactService 기본 테스트
- DAILY_REPORT, TEST_RESULT_REPORT, ISSUE_STATUS_REPORT, WORKLOG_SUMMARY 산출물 유형 확장
- 일일 업무보고 Markdown 생성 및 다운로드
- 테스트 결과 보고서 Markdown 미리보기 및 다운로드
- 테스트 결과 보고서 Apache POI 기반 `.xlsx` 다운로드
- 테스트 결과 보고서 생성 이력 저장
- DailyReportMarkdownGenerator, TestResultReportMarkdownGenerator, TestResultReportExcelGenerator 테스트
- TEST_EVIDENCE 테이블 Flyway 마이그레이션
- 프로젝트/이슈 기준 테스트 증적 등록, 수정, 삭제, 상세, 목록 조회
- 테스트 판정 SUCCESS, FAIL, BLOCKED 관리
- 스크린샷 로컬 업로드 및 조회
- 테스트 증적 Thymeleaf 화면
- TestEvidenceService 기본 단위 테스트
- WBS_TASK, WBS_TASK_DEPENDENCY 테이블 Flyway 마이그레이션
- 프로젝트별 WBS 계층 작업 등록, 수정, 삭제, 상세, 목록 조회
- WBS 코드 자동 생성
- 이슈 연결 및 실제 공수 반영
- 완료 후 시작(FINISH_TO_START) 선후행 작업 등록
- Thymeleaf 기반 WBS Gantt 화면
- WBS Gantt 지연, 공수 초과, 선행 작업 미완료 위험 표시
- WbsService 기본 단위 테스트
- 프로젝트별 칸반 보드 조회
- REGISTERED, ANALYZING, IN_PROGRESS, DEV_DONE, TESTING, DONE 상태 컬럼 표시
- 담당자, 우선순위, 키워드 필터
- 드래그 앤 드롭 기반 이슈 상태 변경 API
- 지연 이슈 강조 및 WIP 제한 설계 표시
- BoardService 기본 단위 테스트
- 프로젝트, 이슈, 공수, 칸반, WBS, 테스트 증적, 산출물 기준 통합 운영 대시보드
- 핵심 지표, 오늘의 주의 항목, 프로젝트 건강도, 업무 흐름, 빠른 실행 영역
- Dashboard 전용 DTO, DAO, Service, MyBatis Mapper
- DashboardService 기본 단위 테스트
- SPRINT, SPRINT_ISSUE 테이블 Flyway 마이그레이션
- 프로젝트별 백로그 이슈 조회
- 스프린트 생성, 수정, 시작, 종료, 삭제
- 백로그 이슈의 스프린트 배정 및 제외
- 스프린트 진행률, 지연, 예상/실제 공수 요약
- 칸반 보드 스프린트 필터
- SprintService 기본 단위 테스트
- 스프린트 분석 리포트 화면
- 스프린트 일자별 스냅샷 기반 Burndown Chart
- SPRINT_DAILY_SNAPSHOT 테이블 Flyway 마이그레이션
- 스프린트 시작, 리포트 조회, 스프린트 이슈 추가/제외, 이슈 상태 변경, 작업 공수 변경 시 당일 스냅샷 갱신
- 저장된 REMAINING_ESTIMATED_MINUTES 기준 실제 잔여 공수선과 SPENT_MINUTES 기준 누적 공수선 표시
- 스프린트 상태별 이슈 분포, 담당자별 작업량, 위험 이슈 조회
- 실패/차단 테스트 증적 표시
- 스프린트 리포트 API 및 Burndown API
- SprintService 리포트 기본 테스트 및 SprintSnapshotService 기본 테스트

## 아직 구현하지 않은 기능

- 기타 산출물 생성
- Gantt 드래그 앤 드롭 일정 조정
- 자동 일정 재계산, 리소스 제약, Critical Path
- 외부 API 연동
- React 전환
- JPA 전환
- 결제 기능
- 메일/Slack 알림
- Gantt Chart
