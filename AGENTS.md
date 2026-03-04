# AGENTS.md

이 파일은 `ahhachul_backend` 작업용 초기 에이전트 가이드입니다.

## 1) 브랜치 정책 (강제)
- 절대 `main`, `develop`에서 직접 작업/커밋/푸시하지 않는다.
- 기본 작업 브랜치는 `codex/main`으로 유지한다.
- 기능 작업은 필요 시 `codex/*` 브랜치로 분기해서 진행한다.
- 최종 반영은 `codex/main` 기준으로만 머지한다.

## 2) 저장소 구조
- 멀티 모듈: `application`, `core`, `consumer`, `scheduler`
- 서브모듈: `ahachul_secret`

## 3) 기본 실행
```bash
./gradlew :application:bootRun --args='--spring.profiles.active=local'
```

## 4) 기본 검증
```bash
./gradlew test
```

## 5) 작업 원칙
- 변경은 최소 범위로 수행하고, 기존 모듈 경계를 유지한다.
- API/비즈니스 로직 수정 시 관련 테스트를 함께 갱신한다.
- 민감정보는 `ahachul_secret`에만 두고 코드에 하드코딩하지 않는다.
