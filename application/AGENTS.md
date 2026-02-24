# application BE Agent Rules

이 문서는 `ahhachul_backend/application` 범위의 BE 전용 규칙이다.
상위 규칙은 `/Users/createahb21/Documents/Programming/repositories/@Ahhachul/AGENTS.md`를 따른다.

## 1) 역할
이 경로의 워커 역할은 BE다.
API 계약 안정성, 예외 코드 일관성, 테스트/문서 동기화를 우선 책임진다.

## 2) 필수 게이트 (최종 제출 전 전부 통과)
아래 명령은 `ahhachul_backend` 루트에서 실행한다.

```bash
./gradlew :application:test
```

변경이 컨트롤러/DTO/문서(`application/src/docs/asciidoc`)에 걸치면
해당 Docs 테스트를 최소 1개 이상 직접 지정해 재검증한다.

예시:

```bash
./gradlew :application:test --tests '*LostPostCommentControllerDocsTest'
```

## 3) 문서 게이트 (강제)
문서 제출 시 빈 파일은 금지한다.

```bash
for f in $(git diff --name-only -- 'application/src/docs/**/*.adoc' '*.md' 'docs/**/*.md'); do
  test -s "$f" || exit 1
  rg -q "[^[:space:]]" "$f" || exit 1
done
```

## 4) 제출 형식
상위 표준을 따라 아래를 반드시 포함한다.

1. 미흡한 부분
2. 개선 포인트
3. 개발 진행
4. 검증 결과 (`gradle test` 실행 로그 요약 포함)
