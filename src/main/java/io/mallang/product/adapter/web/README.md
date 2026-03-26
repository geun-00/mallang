# API 목록

* [상품 등록](#상품-등록)
* [상품 정보 수정](#상품-정보-수정)
* [재고 추가](#재고-추가)
* [재고 차감](#재고-차감)
* [판매 중단](#판매-중단)
* [이미지 추가](#이미지-추가)
* [이미지 삭제](#이미지-삭제)
* [대표 이미지 변경](#대표-이미지-변경)

---

### 상품 등록

#### 요청
- `POST /products`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/products' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "상품명",
    "description": "상품 설명",
    "price": 10000,
    "stockQuantity": 100,
    "category": "FOOD",
    "images": [
      { "imageUrl": "https://example.com/images/thumbnail.jpg", "isThumbnail": true },
      { "imageUrl": "https://example.com/images/image1.jpg", "isThumbnail": false }
    ]
  }'
  ```

#### 성공 응답
- 상태코드: `201 Created`
- 헤더
  ```
  Location: /products/{productId}
  ```

#### 실패 응답
- 상태코드:
  - `400 Bad Request`

#### 정책
- 인증이 필요하다
- `name`은 필수다
- `description`은 필수다 (빈 문자열 허용)
- `price`는 필수다
- `stockQuantity`는 필수다
- `category`는 필수다
- `category`는 `FOOD`, `ELECTRONICS`, `CLOTHING`, `BOOKS`, `ETC` 중 하나이다
- `images`가 있는 경우 대표 이미지(`isThumbnail: true`)는 반드시 하나여야 한다
- 판매자는 인증된 사용자로 자동 설정된다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [x] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [x] 이미지 없이 등록할 수 있다
- [x] 이미지와 함께 등록할 수 있다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `name` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `description` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `price` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `stockQuantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `category` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다

---

### 상품 정보 수정

#### 요청
- `PUT /products/{productId}`
- curl 명령 예시
  ```bash
  curl -i -X PUT 'http://localhost:8080/products/{productId}' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "수정된 상품명",
    "description": "수정된 상품 설명",
    "price": 20000,
    "category": "BOOKS"
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
- `name`은 필수다
- `description`은 필수다 (빈 문자열 허용)
- `price`는 필수다
- `category`는 필수다
- `category`는 `FOOD`, `ELECTRONICS`, `CLOTHING`, `BOOKS`, `ETC` 중 하나이다
- 판매자 본인의 상품만 수정할 수 있다
- `DISCONTINUED` 상품은 수정할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `name` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `description` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `price` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] `category` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 재고 추가

#### 요청
- `PATCH /products/{productId}/stock/add`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/products/{productId}/stock/add' \
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
- 판매자 본인의 상품만 재고를 추가할 수 있다
- `DISCONTINUED` 상품은 재고를 추가할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 재고 차감

#### 요청
- `PATCH /products/{productId}/stock/deduct`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/products/{productId}/stock/deduct' \
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
- 판매자 본인의 상품만 재고를 차감할 수 있다
- `DISCONTINUED` 상품은 재고를 차감할 수 없다
- 보유 재고보다 많은 수량을 차감할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `quantity` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 판매 중단

#### 요청
- `PATCH /products/{productId}/discontinue`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/products/{productId}/discontinue'
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
- 판매자 본인의 상품만 판매 중단할 수 있다
- 이미 `DISCONTINUED` 상태인 상품은 중단할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 이미지 추가

#### 요청
- `POST /products/{productId}/images`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/products/{productId}/images' \
  -H 'Content-Type: application/json' \
  -d '{
    "imageUrls": [
      "https://example.com/images/image1.jpg",
      "https://example.com/images/image2.jpg"
    ]
  }'
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
- `imageUrls`는 필수다
- 판매자 본인의 상품만 이미지를 추가할 수 있다
- `DISCONTINUED` 상품은 이미지를 추가할 수 없다
- 대표 이미지 외 이미지는 최대 10개까지 등록할 수 있다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] `imageUrls` 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 이미지 삭제

#### 요청
- `DELETE /products/{productId}/images/{imageId}`
- curl 명령 예시
  ```bash
  curl -i -X DELETE 'http://localhost:8080/products/{productId}/images/{imageId}'
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
- 판매자 본인의 상품만 이미지를 삭제할 수 있다
- `DISCONTINUED` 상품은 이미지를 삭제할 수 없다
- 존재하지 않는 이미지 ID로 삭제할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 존재하지 않는 이미지이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다

---

### 대표 이미지 변경

#### 요청
- `PATCH /products/{productId}/images/{imageId}/thumbnail`
- curl 명령 예시
  ```bash
  curl -i -X PATCH 'http://localhost:8080/products/{productId}/images/{imageId}/thumbnail'
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
- 판매자 본인의 상품만 대표 이미지를 변경할 수 있다
- `DISCONTINUED` 상품은 대표 이미지를 변경할 수 없다
- 존재하지 않는 이미지 ID로 변경할 수 없다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 인증되지 않은 요청이면 로그인 페이지로 리다이렉트한다
- [x] 존재하지 않는 상품이면 `404 Not Found` 상태코드를 반환한다
- [x] 존재하지 않는 이미지이면 `404 Not Found` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
- [x] 본인 상품이 아니면 `403 Forbidden` 상태코드를 반환한다
