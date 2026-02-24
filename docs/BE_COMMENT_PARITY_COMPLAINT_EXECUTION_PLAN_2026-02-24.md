# BE 민원 댓글 동등화 실행 계획 (2026-02-24)

## 1. 미흡한 부분
- 민원 댓글은 조회/생성만 post-scoped API를 제공하며, 수정/삭제의 post-scoped API가 없다.
- 커뮤니티/유실물 대비 API 형태가 비대칭이라 FE 계약 일관성이 낮다.

## 2. 개선 포인트
- 민원 댓글도 커뮤니티와 동일한 post-scoped 수정/삭제 API를 제공한다.
- 기존 `/v1/comments/{commentId}`는 호환성 유지를 위해 유지하고, 민원 전용 경로를 추가한다.

## 3. 개발 진행
1. `ComplaintPostCommentController`에 아래 엔드포인트 추가
   - `PATCH /v1/complaint-posts/{postId}/comments/{commentId}`
   - `DELETE /v1/complaint-posts/{postId}/comments/{commentId}`
2. postId/commentId 스코프 검증을 강제
3. docs test 및 예외 케이스 테스트 보강

## 4. 검증 결과(계획)
- `./gradlew --no-daemon :application:compileKotlin :application:compileTestKotlin`
- `./gradlew --no-daemon :application:test --tests '*ComplaintPostCommentControllerDocsTest'`

## 5. 비범위
- 유실물 댓글 신규 기능 추가 없음(회귀 확인만 수행).
