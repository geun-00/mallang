# API 목록

* [재고 추가](#재고-추가)
* [재고 차감](#재고-차감)

---

### 재고 추가

#### 요청
- `POST /stocks/{productId}/add`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/stocks/{productId}/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "quantity": 10
  }'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `404 Not Found`
  - `403 Forbidden`

#### 정책
- 인증이 필요하다
- `quantity`는 필수, 양수값이어야 한다
- 판매자 본인의 상품 재고만 추가할 수 있다
- `DISCONTINUED` 상품의 재고는 추가할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 재고 차감

#### 요청
- `POST /stocks/{productId}/deduct`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/stocks/{productId}/deduct' \
  -H 'Content-Type: application/json' \
  -d '{
    "quantity": 5
  }'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `404 Not Found`
  - `403 Forbidden`

#### 정책
- 인증이 필요하다
- `quantity`는 필수, 양수값이어야 한다
- 판매자 본인의 상품 재고만 차감할 수 있다
- `DISCONTINUED` 상품의 재고는 차감할 수 없다
- 보유 재고보다 많은 수량을 차감할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다
