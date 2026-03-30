# Cart 도메인

---

## 도메인 모델

### 장바구니 (Cart)
_Aggregate Root_

#### 행위 & 규칙
- 장바구니 생성 : `static create(MemberId memberId)`
  - `MemberId`가 장바구니의 식별자를 겸한다
  - 생성 시 CartItems는 비어 있다
- 상품 추가 : `addItem(AddCartItemCommand command, IdGenerator idGenerator)`
  - 이미 담긴 상품(`ProductId` 기준)이면 수량을 합산한다
  - 새 CartItem을 생성하지 않고 기존 CartItem의 수량만 증가시킨다
  - 수량은 1 이상이어야 한다
- 수량 변경 : `changeQuantity(CartItemId itemId, int quantity)`
  - 존재하지 않는 `CartItemId`이면 예외가 발생한다
  - 변경 수량은 1 이상이어야 한다
- 항목 제거 : `removeItem(CartItemId itemId)`
  - 존재하지 않는 `CartItemId`이면 예외가 발생한다
- 선택 항목 제거 : `removeItems(List<CartItemId> itemIds)`
  - 여러 장바구니 항목을 한 번에 제거한다
  - 빈 목록이면 예외 없이 정상 처리된다
- 전체 비우기 : `clear()`
  - 이미 비어 있는 장바구니여도 예외 없이 정상 처리된다
- 동일 상품 누적 수량 조회 : `getQuantityOf(ProductId productId)`
  - 상품 추가 시 재고 검증을 위해 사용한다
- 장바구니 항목의 상품 ID 조회 : `getProductIdOf(CartItemId itemId)`
  - 수량 변경 시 대상 상품 조회를 위해 사용한다
  - 존재하지 않는 `CartItemId`이면 예외가 발생한다
- 담긴 상품 ID 목록 반환 : `getProductIds()`
  - 장바구니에 담긴 상품 목록 조회에 사용한다

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `memberId` | MemberId | 장바구니 소유 회원 식별자 |
| `items` | CartItems | 장바구니 항목 컬렉션 |

---

### 장바구니 항목 목록 (CartItems)
_Value Object / 일급 컬렉션_

#### 속성 & 규칙
- `items` : List<CartItem>
- CartItem 컬렉션을 관리한다
- 상품 추가, 수량 변경, 단건 제거, 다건 제거, 전체 비우기 책임을 가진다
- `findById(CartItemId itemId)`로 항목을 조회한다
- 존재하지 않는 항목이면 `InvalidValueException`이 발생한다

---

### 장바구니 항목 (CartItem)
_Entity_

#### 행위 & 규칙
- 수량 추가 : `addQuantity(int quantity)`
  - 수량은 1 이상이어야 한다
- 수량 변경 : `changeQuantity(int quantity)`
  - 수량은 1 이상이어야 한다

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | CartItemId | 식별자 |
| `productId` | ProductId | 장바구니에 담긴 상품 (상품 애그리거트 참조) |
| `quantity` | int | 담은 수량 |

---

## 테스트 시나리오

### Cart

#### 장바구니 생성
- [x] 유효한 MemberId로 장바구니를 생성할 수 있다
- [x] 장바구니를 생성하면 항목 목록이 비어 있다

#### 상품 추가
- [x] 새로운 상품을 추가하면 CartItem이 생성된다
- [x] 이미 담긴 상품을 추가하면 새 CartItem이 생성되지 않고 수량이 합산된다
- [x] 서로 다른 상품을 추가하면 각각 별도의 CartItem으로 추가된다
- [x] 수량이 0이면 예외가 발생한다
- [x] 수량이 음수이면 예외가 발생한다

#### 수량 변경
- [x] 수량을 변경하면 CartItem의 수량이 변경된다
- [x] 존재하지 않는 CartItemId로 수량을 변경하면 예외가 발생한다
- [x] 변경 수량이 0이면 예외가 발생한다
- [x] 변경 수량이 음수이면 예외가 발생한다

#### 항목 제거
- [x] CartItem을 제거할 수 있다
- [x] 존재하지 않는 CartItemId로 제거하면 예외가 발생한다

#### 선택 항목 제거
- [x] 여러 CartItem을 한 번에 제거할 수 있다
- [x] 빈 목록으로 호출하면 예외 없이 정상 처리된다

#### 전체 비우기
- [x] 전체 비우기를 하면 CartItems가 비어 있다
- [x] 이미 비어 있는 장바구니에서 전체 비우기를 해도 예외 없이 정상 처리된다

#### 조회
- [x] 담긴 상품 ID 목록을 반환한다
- [x] 비어 있는 장바구니에서 상품 ID 목록을 조회하면 빈 목록이 반환된다
