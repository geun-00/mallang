# Product 도메인

## 도메인 모델

### 상품 (Product)
_Aggregate Root_

#### 행위 & 규칙
- 상품 생성 : `static create(ProductCreateCommand command, MemberId sellerId, IdGenerator idGenerator)`
  - 재고가 1 이상이면 `ON_SALE`, 0이면 `SOLD_OUT` 상태로 생성
  - 생성 시 식별자(`id`) 할당
  - 이미지가 있는 경우 대표 이미지는 반드시 하나여야 한다
  - 대표 이미지는 `thumbnailImage`에, 나머지는 `images`에 저장
- 재고 추가 : `addStock(quantity)`
  - `DISCONTINUED` 상태인 상품은 재고를 추가할 수 없다
  - 재고 추가 후 수량이 1 이상이 되면 자동으로 `ON_SALE` 전환
- 재고 차감 : `deductStock(quantity)`
  - `DISCONTINUED` 상태인 상품은 재고를 차감할 수 없다
  - 보유 재고보다 많은 수량을 차감할 수 없다
  - 재고 차감 후 수량이 0이 되면 자동으로 `SOLD_OUT` 전환
- 상품 정보 수정 : `modify(ModifyProductCommand command)`
  - `DISCONTINUED` 상태인 상품은 수정할 수 없다
- 판매 중단 : `discontinue()`
  - 이미 `DISCONTINUED` 상태인 상품은 중단할 수 없다
  - `DISCONTINUED`는 불가역 상태로 복구 불가
- 이미지 추가 : `addImages(List<AddProductImageCommand> commands, IdGenerator idGenerator)`
  - `DISCONTINUED` 상태인 상품은 이미지를 추가할 수 없다
  - 이미지는 최대 10개까지 등록 가능
- 이미지 삭제 : `removeImage(ProductImageId imageId)`
  - `DISCONTINUED` 상태인 상품은 이미지를 삭제할 수 없다
  - 존재하지 않는 이미지 ID로 삭제 시 예외 발생
- 대표 이미지 변경 : `changeThumbnailImage(ProductImageId imageId)`
  - `DISCONTINUED` 상태인 상품은 대표 이미지를 변경할 수 없다
  - 존재하지 않는 이미지 ID로 변경 시 예외 발생

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | ProductId | 식별자 |
| `sellerId` | MemberId | 판매자 (회원 애그리거트 참조) |
| `name` | ProductName (VO) | 상품명 |
| `description` | ProductDescription (VO) | 상품 설명 |
| `price` | Money (VO) | 가격 |
| `stockQuantity` | StockQuantity (VO) | 재고 수량 |
| `status` | ProductStatus | 상품 상태 |
| `category` | ProductCategory | 상품 카테고리 |
| `productImages` | ProductImages (VO) | 이미지 묶음 |

---

### 상품 상태 (ProductStatus)
_Enum_

| 값 | 설명 |
|----|------|
| `ON_SALE` | 판매 중 |
| `SOLD_OUT` | 품절 (재고 = 0) |
| `DISCONTINUED` | 판매 중단 (불가역) |

---

### 상품 카테고리 (ProductCategory)
_Enum_

| 값 | 설명 |
|----|------|
| `FOOD` | 식품 |
| `ELECTRONICS` | 전자기기 |
| `CLOTHING` | 의류 |
| `BOOKS` | 도서 |
| `ETC` | 기타 |

---

### 상품 이미지 묶음 (ProductImages)
_Value Object_

#### 속성 & 규칙
- `thumbnailImage` : ProductImage (nullable)
- `images` : List\<ProductImage\>
- 이미지가 있는 경우 대표 이미지는 반드시 하나여야 한다
- 대표 이미지 외 이미지는 최대 10개까지 등록 가능

---

### 상품 이미지 (ProductImage)
_Entity_

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | ProductImageId | 식별자 |
| `imageUrl` | ImageUrl (VO) | 이미지 URL |

---

### 상품명 (ProductName)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | String | null·공백 불가 / 앞뒤 공백 불가 / 1자 이상 100자 이하 |

---

### 상품 설명 (ProductDescription)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | String | null 불가 / 최대 2000자 / 빈 문자열("") 허용 / 앞뒤 공백 자동 제거(strip) |

---

### 금액 (Money)
_Value Object — `domain.common.vo` 참조_

→ [`domain/common/README.md`](../../../domain/common/README.md)

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `amount` | BigDecimal | null 불가 / 음수 불가 (0 허용 — 무료 상품) |

---

### 재고 수량 (StockQuantity)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | int | 음수 불가 (0 허용 — 품절 상태 표현) |

---

### 이미지 URL (ImageUrl)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | String | null·공백 불가 / `http://` 또는 `https://`로 시작 |

---

## 테스트 시나리오

### ProductImages

- [x] 이미지와 함께 상품을 생성하면 대표이미지와 이미지목록이 저장된다
- [x] 이미지 없이 상품을 생성하면 대표이미지와 이미지목록이 비어있다
- [x] 이미지가 있는데 대표이미지가 없으면 예외가 발생한다
- [x] 대표이미지 외 이미지가 10개를 초과하면 예외가 발생한다

### ProductName

- [x] 유효한 형식으로 상품명을 생성할 수 있다
- [x] 상품명은 null 또는 공백이 아니어야 한다
- [x] 상품명은 앞뒤 공백이 없어야 한다
- [x] 상품명은 100자 이하여야 한다

### Money

- [x] 금액은 null 또는 음수가 아니어야 한다
- [x] 금액이 0 이상이면 정상 생성된다
### StockQuantity

- [x] 수량은 null 또는 음수가 아니어야 한다
- [x] 수량이 0 이상이면 정상 생성된다

### ImageUrl

- [x] URL은 null 또는 공백이 아니어야 한다
- [x] URL은 유효한 형식이어야 한다
- [x] 유효한 URL은 정상 생성된다

### Product

#### 상품 생성
- [x] 유효한 정보로 상품을 생성하면 식별자가 할당된다
- [x] 유효한 정보로 상품을 생성하면 상품명, 설명, 가격, 재고 수량, 카테고리가 저장된다
- [x] 이미지와 함께 상품을 생성하면 대표이미지와 이미지목록이 저장된다
- [x] 이미지 없이 상품을 생성하면 대표이미지와 이미지목록이 비어있다
- [x] 재고가 1 이상이면 ON_SALE 상태로 생성된다
- [x] 재고가 0이면 SOLD_OUT 상태로 생성된다

#### 재고 관리
- [x] 재고를 추가하면 수량이 증가한다
- [x] 재고를 차감하면 수량이 감소한다
- [x] 재고 차감 후 0이 되면 SOLD_OUT 상태가 된다
- [x] 재고 추가 후 1 이상이 되면 ON_SALE 상태가 된다
- [x] 재고 차감 시 보유 재고보다 많은 수량을 차감하면 예외가 발생한다
- [x] `DISCONTINUED` 상품은 재고를 추가할 수 없다
- [x] `DISCONTINUED` 상품은 재고를 차감할 수 없다

#### 상품 정보 수정
- [x] 상품 정보를 수정할 수 있다
- [x] `DISCONTINUED` 상품은 수정할 수 없다

#### 판매 중단
- [x] 판매 중단하면 DISCONTINUED 상태가 된다
- [x] 이미 DISCONTINUED 상태에서 판매 중단하면 예외가 발생한다

#### 이미지 추가
- [x] 이미지를 추가하면 식별자와 URL이 저장된다
- [x] 이미지가 없는 상태에서 이미지를 추가하면 첫 번째 이미지가 대표이미지가 된다
- [x] 대표이미지가 있는 상태에서 이미지를 추가하면 모든 이미지는 일반 이미지로 추가된다
- [x] 대표이미지 외 이미지는 최대 10개까지만 추가할 수 있다
- [x] 빈 리스트로 이미지 추가를 호출하면 예외 없이 정상 처리된다
- [x] `DISCONTINUED` 상품은 이미지를 추가할 수 없다

#### 이미지 삭제
- [x] 일반 이미지를 삭제할 수 있다
- [x] 대표이미지만 있는 상태에서 대표이미지를 삭제하면 이미지가 없는 상태가 된다
- [x] 대표이미지를 삭제하면 남은 이미지 중 첫 번째가 대표이미지가 된다
- [x] 존재하지 않는 이미지 ID로 삭제하면 예외가 발생한다
- [x] `DISCONTINUED` 상품은 이미지를 삭제할 수 없다

#### 대표이미지 변경
- [x] 대표이미지를 변경하면 기존 대표이미지는 일반 이미지로 전환된다
- [x] 대표이미지로 변경하려는 이미지가 존재하지 않으면 예외가 발생한다
- [x] 이미 대표이미지인 이미지를 대표이미지로 변경하면 상태는 그대로 유지된다
- [x] `DISCONTINUED` 상품은 대표이미지를 변경할 수 없다
