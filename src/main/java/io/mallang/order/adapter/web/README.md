# API 목록

* [주문 생성](#주문-생성)
* [주문 취소](#주문-취소)
* [내 주문 목록 조회](#내-주문-목록-조회)
* [내 주문 상세 조회](#내-주문-상세-조회)

---

### 주문 생성

#### 요청
- `POST /my/orders`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/my/orders' \
  -H 'Content-Type: application/json' \
  -d '{
    "items": [
      {
        "productId": "product-1",
        "quantity": 2
      },
      {
        "productId": "product-2",
        "quantity": 1
      }
    ],
    "receiverName": "홍길동",
    "receiverPhoneNumber": "01012345678",
    "zipCode": "12345",
    "mainAddress": "서울시 강남구 테헤란로 1",
    "detailAddress": "101호"
  }'
  ```

#### 성공 응답
- 상태코드: `201 Created`
- 헤더
  ```
  Location: /my/orders/{orderId}
  ```

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `403 Forbidden`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 주문자는 인증된 사용자로 자동 설정된다
- `items`는 필수다
- 주문 상품은 1개 이상이어야 한다
- 각 주문 상품의 `productId`는 필수다
- 각 주문 상품의 `quantity`는 필수이며 1 이상이어야 한다
- `receiverName`은 필수다
- `receiverPhoneNumber`는 필수다
- `zipCode`는 필수다
- `mainAddress`는 필수다
- 상품 가격은 요청으로 받지 않고 서버가 조회한 현재 상품 가격을 사용한다
- 존재하지 않는 회원은 주문할 수 없다
- 주문할 수 없는 회원은 주문할 수 없다
- 존재하지 않는 상품으로 주문할 수 없다
- 상품 재고보다 많은 수량을 주문할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [x] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [x] 올바르게 요청하면 `Location` 헤더의 식별자로 주문을 조회할 수 있다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `items` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `receiverName` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `receiverPhoneNumber` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `zipCode` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `mainAddress` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 주문할 수 없는 회원이면 `403 Forbidden` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 주문 취소

#### 요청
- `PATCH /my/orders/{orderId}/cancel`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/my/orders/{orderId}/cancel'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `403 Forbidden`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 주문자는 인증된 사용자로 자동 설정된다
- 본인 주문만 취소할 수 있다
- `PAYMENT_WAITING`, `PREPARING` 상태인 주문만 취소할 수 있다
- 주문 취소 시 주문 상품 재고는 원복된다
- 존재하지 않는 주문은 취소할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] 존재하지 않는 주문이면 `404 Not Found` 상태코드를 반환한다
- [x] 본인 주문이 아니면 `403 Forbidden` 상태코드를 반환한다
- [x] 취소할 수 없는 상태의 주문이면 `400 Bad Request` 상태코드를 반환한다

---

### 내 주문 목록 조회

#### 요청
- `GET /my/orders`
- Query Parameters
  - `status` : 주문 상태 (선택)
  - `lastOrderId` : 다음 slice 조회 기준 주문 ID (선택)
  - `size` : 조회 크기 (선택, 기본값 `20`)
- curl 명령 예시
  ```bash
  curl -i -X GET 'http://localhost:8080/my/orders?status=PAYMENT_WAITING&size=20'
  ```

#### 성공 응답
- 상태코드: `200 OK`
- 응답 본문
  ```json
  {
    "items": [
      {
        "orderId": "order-1",
        "status": "PAYMENT_WAITING",
        "orderedAt": "2024-01-01T00:00:00",
        "totalPrice": 6000,
        "itemCount": 2,
        "mainProductId": "product-1",
        "mainProductName": "사과",
        "mainProductThumbnailImageUrl": "https://example.com/apple.jpg"
      }
    ],
    "hasNext": true,
    "nextCursor": "order-1"
  }
  ```

#### 실패 응답
- 상태코드:
  - `401 Unauthorized`

#### 정책
- 인증이 필요하다
- 인증된 사용자의 주문만 조회한다
- `status`가 지정되면 해당 주문 상태만 조회한다
- `lastOrderId`가 지정되면 해당 주문 다음 slice를 조회한다
- 주문 목록은 주문일시 내림차순, 주문 ID 내림차순으로 조회한다
- 응답의 `nextCursor`는 다음 slice 조회에 사용할 마지막 주문 ID다
- 주문 목록의 대표 상품은 주문 상품 중 가장 작은 주문 상품 ID를 가진 상품이다

#### 테스트 시나리오
- [x] 내 주문 목록을 조회할 수 있다
- [x] `status`로 내 주문 목록을 필터링할 수 있다
- [x] `size`를 지정하면 `hasNext`와 `nextCursor`를 반환한다
- [x] 인증되지 않은 요청이면 `401 Unauthorized` 상태코드를 반환한다

---

### 내 주문 상세 조회

#### 요청
- `GET /my/orders/{orderId}`
- Path Variables
  - `orderId` : 조회할 주문 ID
- curl 명령 예시
  ```bash
  curl -i -X GET 'http://localhost:8080/my/orders/{orderId}'
  ```

#### 성공 응답
- 상태코드: `200 OK`
- 응답 본문
  ```json
  {
    "orderId": "order-1",
    "memberId": "member-1",
    "status": "PAYMENT_WAITING",
    "orderedAt": "2024-01-01T00:00:00",
    "totalPrice": 6000,
    "receiverName": "홍길동",
    "receiverPhoneNumber": "01012345678",
    "zipCode": "12345",
    "mainAddress": "서울시 강남구 테헤란로 1",
    "detailAddress": "101호",
    "items": [
      {
        "orderItemId": "order-item-1",
        "productId": "product-1",
        "productName": "사과",
        "productThumbnailImageUrl": "https://example.com/apple.jpg",
        "price": 3000,
        "quantity": 2,
        "totalPrice": 6000
      }
    ]
  }
  ```

#### 실패 응답
- 상태코드:
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 본인 주문만 조회할 수 있다
- 존재하지 않는 주문은 조회할 수 없다
- 주문 상품은 주문 상품 금액 내림차순, 주문 상품 ID 오름차순으로 조회한다
- 주문 상품의 `price`는 주문 당시 상품 단가다
- 주문 상품의 `totalPrice`는 주문 당시 상품 단가와 주문 수량을 곱한 금액이다

#### 테스트 시나리오
- [x] 내 주문 상세를 조회할 수 있다
- [x] 인증되지 않은 요청이면 `401 Unauthorized` 상태코드를 반환한다
- [x] 존재하지 않는 주문이면 `404 Not Found` 상태코드를 반환한다
- [x] 본인 주문이 아니면 `403 Forbidden` 상태코드를 반환한다
