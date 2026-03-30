# API 목록

* [장바구니 상품 추가](#장바구니-상품-추가)
* [장바구니 상품 수량 변경](#장바구니-상품-수량-변경)
* [장바구니 상품 제거](#장바구니-상품-제거)
* [장바구니 전체 비우기](#장바구니-전체-비우기)

---

### 장바구니 상품 추가

#### 요청
- `POST /my/cart/items`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/my/cart/items' \
  -H 'Content-Type: application/json' \
  -d '{
    "productId": "product-1",
    "quantity": 2
  }'
  ```

#### 성공 응답
- 상태코드: `201 Created`
- 헤더
  ```
  Location: /my/cart/items/{cartItemId}
  ```

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 장바구니 소유자는 인증된 사용자로 자동 설정된다
- `productId`는 필수다
- `quantity`는 필수이며 1 이상이어야 한다
- 존재하지 않는 장바구니면 추가할 수 없다
- 존재하지 않는 상품은 추가할 수 없다
- 기존에 같은 상품이 있으면 수량이 합산된다
- 합산 결과가 상품 재고보다 크면 추가할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [ ] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [ ] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [ ] `productId` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] 존재하지 않는 장바구니면 `404 Not Found` 상태코드를 반환한다
- [ ] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [ ] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 장바구니 상품 수량 변경

#### 요청
- `PATCH /my/cart/items/{cartItemId}`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/my/cart/items/{cartItemId}' \
  -H 'Content-Type: application/json' \
  -d '{
    "quantity": 3
  }'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 장바구니 소유자는 인증된 사용자로 자동 설정된다
- `quantity`는 필수이며 1 이상이어야 한다
- 존재하지 않는 장바구니면 변경할 수 없다
- 존재하지 않는 장바구니 항목이면 변경할 수 없다
- 존재하지 않는 상품이면 변경할 수 없다
- 변경 수량이 상품 재고보다 크면 변경할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [ ] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [ ] 존재하지 않는 장바구니면 `404 Not Found` 상태코드를 반환한다
- [ ] 존재하지 않는 장바구니 항목이면 `400 Bad Request` 상태코드를 반환한다
- [ ] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [ ] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 장바구니 상품 제거

#### 요청
- `DELETE /my/cart/items/{cartItemId}`
- curl 명령 예시
  ```bash
  curl -i -X DELETE 'http://localhost:8080/my/cart/items/{cartItemId}'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 장바구니 소유자는 인증된 사용자로 자동 설정된다
- 존재하지 않는 장바구니면 제거할 수 없다
- 존재하지 않는 장바구니 항목이면 제거할 수 없다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [ ] 존재하지 않는 장바구니면 `404 Not Found` 상태코드를 반환한다
- [ ] 존재하지 않는 장바구니 항목이면 `400 Bad Request` 상태코드를 반환한다

---

### 장바구니 전체 비우기

#### 요청
- `DELETE /my/cart/items`
- curl 명령 예시
  ```bash
  curl -i -X DELETE 'http://localhost:8080/my/cart/items'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `404 Not Found`

#### 정책
- 인증이 필요하다
- 장바구니 소유자는 인증된 사용자로 자동 설정된다
- 존재하지 않는 장바구니면 비울 수 없다
- 이미 비어 있는 장바구니여도 예외 없이 정상 처리된다

#### 테스트 시나리오
- [ ] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [ ] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [ ] 존재하지 않는 장바구니면 `404 Not Found` 상태코드를 반환한다
- [ ] 이미 비어 있는 장바구니여도 `204 No Content` 상태코드를 반환한다
