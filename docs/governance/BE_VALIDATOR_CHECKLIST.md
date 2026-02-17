# BE Validator Checklist (Blocking)

## Required Gates (all must pass)
1. `./gradlew clean test`
2. 변경 모듈 컴파일/실행 확인
   - API 변경 포함: `./gradlew :application:bootRun --args='--spring.profiles.active=local'`
   - scheduler 변경 포함: `./gradlew :scheduler:bootRun --args='--spring.profiles.active=local'`
   - consumer 변경 포함: `./gradlew :consumer:bootRun --args='--spring.profiles.active=local'`
3. API 계약 게이트
   - `CommonResponse`/`ResponseCode`/DTO `toCommand(...)` 패턴 위반 없음
4. 데이터 계약 게이트
   - Flyway 마이그레이션 필요 변경 누락 없음
   - 시크릿/환경키 변경 시 영향 범위 문서화 완료
5. 운영 게이트
   - 로컬 재현 절차(성공/실패 시나리오)가 PR 본문에 기록됨
   - 민감정보(토큰/키/식별자) 노출 없음
6. 문서 게이트
   - 본 태스크로 규칙이 변경되면 `BE_RULEBOOK.md` + `BE_RULEBOOK_CHANGELOG.md` 동시 갱신
   - 태스크 상태/핸드오프/워크로그 동기화 완료

## Rule
- 하나라도 실패하면 태스크 상태를 `Rework`로 되돌립니다.
- Validator 승인 전 커밋 상태를 `Done`으로 표기할 수 없습니다.

