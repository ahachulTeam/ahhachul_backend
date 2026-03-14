# 🚀 AhHachul Backend

**🧜‍♂️ 더욱 쾌적한 지하철을 위한 유저 기반 커뮤니티 플랫폼, ..아... 하철이형! 🧜**

<br />

<img width="1200" alt="image" src="https://github.com/ahachulTeam/ahachul_web/assets/80245801/d29c819c-6b95-4a77-838a-3bdc92a297fb">
</div>

<br />
</div>

## 주요 기능
여러 곳에 흩어져 따로 운영되던 지하철 관련 기능들을 하나의 플랫폼으로 모아, 사용자가 더 간편하게 지하철을 이용할 수 있도록 돕는 서비스입니다.

### 🚇 역 즐겨찾기 및 실시간 열차 도착 정보 조회
- 주로 이용하는 역을 설정하고, 근접한 열차의 실시간 지하철 운행 정보를 확인할 수 있습니다. <br>
<img src="https://github.com/user-attachments/assets/ef9a2745-8df3-47eb-b4f2-8c1d6cf38ad2" style="width: 30%;"/>


### 💼 유실물 및 습득물 관리
- Lost112 사이트에 있던 습득물 데이터를 간편하게 찾고, 사용자가 직접 앱에서 유실물/습득물을 등록하고 찾을 수 있습니다. <br>

<img src="https://github.com/user-attachments/assets/5a70ddf7-6a4e-4e64-95b6-77a8125e8903" style="width: 30%;"/>


### 🗣️ 지하철 문의 보내기
- 지하철 요청 사항이나 문의를 앱에 등록하면 간편하게 서울교통공사에 문자 메시지를 보낼 수 있습니다. <br>
 <img src="https://github.com/user-attachments/assets/92f3a8df-efaf-4111-b836-8e3d258c937b" style="width: 30%;"/>

## 개발 스택

- Spring Boot, Kotlin
- JPA, Data JPA, QueryDSL
- AWS RDS(MySQL), ElastiCache(Redis)
- Spring REST Docs, Junit5, Mockito
- Flyway

## 패키지 구조

헥사고날 아키텍처를 바탕으로 한 멀티 모듈 구조

> core

엔티티, 레포지토리(영속성 계층) 관련 의존성과 로직이 담긴 모듈입니다. <br>
`flyway` 의존성은 해당 모듈에만 존재합니다.

> application

컨트롤러(뷰 계층)와 서비스 관련 의존성과 로직이 담긴 모듈입니다.  <br>
코어 패키지를 공유하고 있으며, `RestDocs` 관련 의존성과 패키지는 해당 모듈에 존재합니다.

> scheduler

스케줄러 관련 의존성과 로직이 담긴 모듈입니다.  <br>
코어 패키지를 공유하고 있습니다.

> consumer

스트림 컨슈머 관련 의존성과 로직이 담긴 모듈입니다. <br>
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

#### Github Action, AWS ECR / ECS, Docker

<img width="836" height="482" alt="스크린샷 2025-11-18 오후 3 55 54" src="https://github.com/user-attachments/assets/61b29c06-e5fa-4634-b108-b840aa03d09a" />


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
