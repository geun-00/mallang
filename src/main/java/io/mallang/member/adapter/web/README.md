# API 목록

* [회원 가입](#회원-가입)
* [배송지 추가](#배송지-추가)
* [기본 배송지 변경](#기본-배송지-변경)
* [배송지 수정](#배송지-수정)
* [배송지 삭제](#배송지-삭제)

---

### 회원 가입

요청
- `POST /members`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/members' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "member1@example.com",
    "password": "password12@",
    "nickname": "닉네임"
  }'
  ```

#### 성공 응답
- 상태코드: `201 Created`
- 헤더
  ```
  Location: /members/{memberId}
  ```

#### 실패 응답
- 상태코드: 
  - `400 Bad Request`
  - `409 Conflict`

#### 정책
- `email`은 필수다
- `password`는 필수다
- `nickname`은 필수다
- 이메일은 유일해야 한다
- 닉네임은 유일해야 한다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [x] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [x] 올바르게 요청하면 `Location` 헤더의 식별자로 회원을 조회할 수 있다
- [x] `email` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `password` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `nickname` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 이미 존재하는 이메일이면 `409 Conflict` 상태코드를 반환한다
- [x] 이미 존재하는 닉네임이면 `409 Conflict` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 배송지 추가

#### 요청
- `POST /my/shipping-addresses`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/my/shipping-addresses' \
  -H 'Content-Type: application/json' \
  -d '{
    "receiverName": "홍길동",
    "receiverPhoneNumber": "01011112222",
    "zipCode": "12345",
    "mainAddress": "서울시 강남구 테헤란로 1",
    "detailAddress": "101호"
  }'
  ```

#### 성공 응답
- 상태코드: `201 Created`
- 헤더
  ```
  Location: /my/shipping-addresses/{shippingAddressId}
  ```

#### 실패 응답
- 상태코드
  - `400 Bad Request`
  - `401 Unauthorized`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- `receiverName`은 필수다
- `receiverPhoneNumber`는 필수다
- `zipCode`는 필수다
- `mainAddress`는 필수다
- 배송지는 최대 5개까지 추가할 수 있다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [x] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `receiverName` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `receiverPhoneNumber` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `zipCode` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `mainAddress` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 기본 배송지 변경

#### 요청
- `PATCH /members/{memberId}/shipping-addresses/{shippingAddressId}/default`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/members/{memberId}/shipping-addresses/{shippingAddressId}/default'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드
  - `400 Bad Request`
  - `401 Unauthorized`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 존재하지 않는 회원의 배송지를 변경할 수 없다
- 본인 배송지가 아니면 기본 배송지로 설정할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 `401 Unauthorized` 상태코드를 반환한다
- [ ] 존재하지 않는 회원이면 `404 Not Found` 상태코드를 반환한다
- [ ] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 배송지 수정

#### 요청
- `PUT /members/{memberId}/shipping-addresses/{shippingAddressId}`
- curl 명령 예시
  ```bash
  curl -i -X PUT 'http://localhost:8080/members/{memberId}/shipping-addresses/{shippingAddressId}' \
  -H 'Content-Type: application/json' \
  -d '{
    "receiverName": "이순신",
    "receiverPhoneNumber": "01099998888",
    "zipCode": "99999",
    "mainAddress": "부산시 해운대구 해운대로 1",
    "detailAddress": "202호"
  }'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드: 
  - `400 Bad Request`
  - `401 Unauthorized`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- `receiverName`은 필수다
- `receiverPhoneNumber`는 필수다
- `zipCode`는 필수다
- `mainAddress`는 필수다
- 존재하지 않는 회원의 배송지를 수정할 수 없다
- 본인 배송지가 아니면 수정할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 `401 Unauthorized` 상태코드를 반환한다
- [ ] `receiverName` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] `receiverPhoneNumber` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] `zipCode` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] `mainAddress` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [ ] 존재하지 않는 회원이면 `404 Not Found` 상태코드를 반환한다

---

### 배송지 삭제

#### 요청
- `DELETE /members/{memberId}/shipping-addresses/{shippingAddressId}`
- curl 명령 예시
  ```bash
  curl -i -X DELETE 'http://localhost:8080/members/{memberId}/shipping-addresses/{shippingAddressId}'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드
  - `400 Bad Request`
  - `401 Unauthorized`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 존재하지 않는 회원의 배송지를 삭제할 수 없다
- 본인 배송지가 아니면 삭제할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 `401 Unauthorized` 상태코드를 반환한다
- [ ] 존재하지 않는 회원이면 `404 Not Found` 상태코드를 반환한다
- [ ] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
