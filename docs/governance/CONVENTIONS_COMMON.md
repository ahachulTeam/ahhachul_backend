# Common Conventions

## Workflow

- Task lifecycle: `Todo -> InProgress -> QAReady -> ValidatorPass -> Committed -> Done`
- Failure path: `* -> Rework -> QAReady`
- One task per commit.

## Branch

- Branch naming for Codex orchestration: `codex/<scope>`

## Merge

- Large refactor/ops PR: `Merge Commit` 우선
- Small/noisy PR: `Squash Merge`
- Hash 추적 문서가 있으면 `Rebase Merge` 지양
