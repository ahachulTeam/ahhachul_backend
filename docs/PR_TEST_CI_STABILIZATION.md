# PR Test CI Stabilization Log

- scope: `.github/workflows/pr-test.yml`
- objective: Ensure PR test runs and passes on `codex/main` based pull requests.
- createdAt: 2026-02-18

## Iteration 1 (Fail)
- PR: #375 (`codex/pr-test-codex-main-20260218` -> `codex/main`)
- run: `22111491885`
- status: failed
- failure point: `Checkout source code`
- evidence:
  - `fatal: could not read Username for 'https://github.com': terminal prompts disabled`
  - custom token path (`token: ${{ env.TOKEN_GITHUB }}`) was used for primary checkout

## Deep Analysis Summary
- `actions/checkout` 기본 토큰은 `${{ github.token }}`이며, 현재 레포 접근에는 기본 토큰 사용이 더 안정적입니다.
- job-level `permissions`를 명시한 상태에서 `contents: read`가 빠지면 기본 토큰 read 권한이 보장되지 않습니다.
- JSON secrets를 그대로 `echo` 후 실행하는 패턴은 값 escaping/마스킹 측면에서 운영 리스크가 있습니다.

## Agreed Fixes
1. PR Test 트리거를 `codex/main`, `develop` 모두 대상으로 유지
2. checkout 토큰을 `${{ github.token }}`로 전환
3. job permissions에 `contents: read` 추가
4. Parse combined secrets 단계에 `::add-mask::` 적용 및 안전한 키/값 주입 방식 사용
5. PR Test에서 submodule checkout은 비활성화(`submodules: false`)하여 1차 안정성 확보

## Verification Plan
1. 수정 커밋 푸시 후 PR Test 재실행
2. 실패 시 run 로그 기준 추가 보정
3. 연속 성공이 확인될 때까지 반복
