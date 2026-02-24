# PM → BE 전달 명세서 (V1)

## 1. 목표
- 커뮤니티/유실물/민원 게시글에 좋아요·북마크 기능 제공
- 사용자 마이페이지에서 좋아요/북마크 히스토리 조회 제공

## 2. 신규/확장 API

### 2.1 좋아요 API
1. `POST /v1/complaint-posts/{postId}/like`
2. `DELETE /v1/complaint-posts/{postId}/like`
3. `POST /v1/lost-posts/{postId}/like`
4. `DELETE /v1/lost-posts/{postId}/like`

- 커뮤니티는 기존 `POST/DELETE /v1/community-posts/{postId}/like` 유지

### 2.2 북마크 API
1. `POST /v1/community-posts/{postId}/bookmark`
2. `DELETE /v1/community-posts/{postId}/bookmark`
3. `POST /v1/complaint-posts/{postId}/bookmark`
4. `DELETE /v1/complaint-posts/{postId}/bookmark`
5. `POST /v1/lost-posts/{postId}/bookmark`
6. `DELETE /v1/lost-posts/{postId}/bookmark`

### 2.3 마이페이지 히스토리 API
1. `GET /v1/members/article-histories?limit=30`
2. 응답
- `likedArticles[]`
- `bookmarkedArticles[]`
- 아이템 필드: `articleType`, `articleId`, `title`, `contentPreview`, `writer`, `subwayLineId`, `stationId`, `articleCreatedAt`, `reactedAt`

## 3. 상세 응답 확장
- 커뮤니티/유실물/민원 상세 응답에 아래 필드 추가
1. `likeCnt`
2. `bookmarkCnt`
3. `likeYn`
4. `bookmarkYn`

## 4. 데이터 모델
- 공통 리액션 저장소
1. `tb_article_like`
- 유니크: `(member_id, article_type, article_id)`
2. `tb_article_bookmark`
- 유니크: `(member_id, article_type, article_id)`

## 5. 정책
- 삭제된 게시글은 반응 불가
- 중복 좋아요/북마크 요청은 `BAD_REQUEST` 계열 에러
- 북마크 해제/좋아요 해제 시 대상 반응이 없으면 에러
- 커뮤니티 기존 좋아요/싫어요 정책은 유지

## 6. 성능/안정성
- 상세 조회 시 count/yn 계산
- 히스토리는 limit 기반 상한 적용 (1~100)
- 삭제된 게시글 이력은 응답에서 제외

## 7. 완료 조건 (DoD)
1. 신규 API 계약 테스트 통과
2. 기존 커뮤니티 좋아요/싫어요 회귀 없음
3. 상세 응답 필드 확장 반영
4. 마이페이지 히스토리 응답 정상
5. BE 게이트(compile/test/docs) 통과
