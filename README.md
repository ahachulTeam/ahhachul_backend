# 🚀 AhHachul Backend

**🧜‍♂️ 더욱 쾌적한 지하철을 위한 유저 기반 커뮤니티 플랫폼, ..아... 하철이형! 🧜**

<br />

<img width="1200" alt="image" src="https://github.com/ahachulTeam/ahachul_web/assets/80245801/d29c819c-6b95-4a77-838a-3bdc92a297fb">
</div>

<br />
</div>

## 개발 스택

- Spring Boot
- Kotlin
- Mysql, JPA, Data JPA, QueryDSL
- Spring REST Docs, Junit5, Mockito

## 패키지 구조

헥사고날 아키텍처를 바탕으로 한 멀티 모듈 구조

> core

엔티티, 레포지토리(영속성 계층) 관련 로직이 담긴 모듈입니다. <br>
`flyway` 의존성은 해당 모듈에만 존재합니다.

> application

컨트롤러(뷰 계층)와 서비스 관련 로직이 담긴 모듈입니다.  <br>
코어 패키지를 공유하고 있으며, `RestDocs` 관련 의존성과 패키지는 해당 모듈에 존재합니다.

> scheduler

스케줄러 관련 의존성, 패키지가 담긴 모듈입니다.  <br>
코어 패키지를 공유하고 있습니다.

```javascript
// 멀티 모듈 분리 이전 기본 헥사고날 구조
|-- ahachul_backend
    |-- <도메인>
        |-- adapter
            |-- in
            |-- out
        |-- application
            |-- port
                |-- in
                |-- out
            |-- service
        |-- domain
    |-- common
        |-- config
        |-- ...
```

## 브랜치 전략

```javascript
|-- main
    |-- develop
        |-- feature/<#issue number>
    |-- hotfix
```


## 배포 파이프라인 구성
- Github Action, AWS ECR, Docker (구버전)
- Github Action, AWS ECR / ECS, Docker (신버전)

<img width="777" alt="image" src="https://github.com/user-attachments/assets/effa4535-3c27-45e2-a098-470e5e9d7309" />


## ERD 
[ERDCloud 링크](https://www.erdcloud.com/d/6dKc9AeJrWc2ZQRNv)

<img width="777" alt="image" src="https://github.com/user-attachments/assets/1aefa163-0845-463c-a50d-c448bbfadb5d">

## 협업 규칙

### 커밋 메시지

- [gitmoji 공식문서](https://gitmoji.dev/)
- **`gitmoji <commit message> (#issue number)`**

| 이모티콘 | 문자 | 설명 | 
| :-------: | :---: | :---: |
|:sparkles: | `sparkles` | 기능 개발 및 기능 수정|
|:bug:| `bug` | 버그 해결 |
|:recycle: | `recycle` | 코드 리팩토링
|:memo: | `memo` | 문서 추가 및 수정
|:closed_lock_with_key: | `closed_lock_with_key` | 설정 파일 업데이트
|:adhesive_bandage: | `adhesive_bandage` | 중요하지 않은 이슈 및 오타 수정
|:white_check_mark:| `white_check_mark` | 테스트 코드 추가 및 수정


### 이슈

1. 이슈 생성 후 PR
2. 코드 리뷰를 통한 피드백 후 `approve`
3. `develop` 브랜치 `merge`

## 코딩 컨벤션

- `save action plugin` 를 사용해서 팀 내 코딩 컨벤션 통합

