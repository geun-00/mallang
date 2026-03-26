# Order 도메인

---

## 도메인 모델

### 주문 (Order)
_Aggregate Root_

#### 행위 & 규칙
- 주문 생성 : `static place(PlaceOrderCommand command, IdGenerator idGenerator, ClockHolder clockHolder)`
  - 생성 시 식별자(`id`)가 할당된다
  - 생성 시 상태는 `PAYMENT_WAITING`이다
  - 주문 시간(`orderedAt`)이 기록된다
  - 주문 상품 목록은 1개 이상이어야 한다
  - 총 가격은 주문 상품 목록의 `(단가 × 수량)` 합산으로 계산된다
  - 주문 시점의 배송지 정보(수령인, 주소)를 스냅샷으로 저장한다
- 주문 복원 : `static restore(RestoreOrderCommand command)`
  - 영속화된 주문을 현재 상태 그대로 복원한다
- 주문 취소 : `cancel()`
  - `PAYMENT_WAITING`, `PREPARING` 상태인 주문만 취소할 수 있다
  - 취소 시 상태는 `CANCELED`가 된다
- 주문 상태 진행 : `nextStatus()`
  - 현재 상태의 다음 상태로 전이한다

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | OrderId | 식별자 |
| `memberId` | MemberId | 주문자 (회원 애그리거트 참조) |
| `items` | OrderItems | 주문 상품 목록 |
| `totalPrice` | Money (VO) | 총 주문 금액 |
| `shippingInfo` | ShippingInfo (VO) | 배송지 스냅샷 |
| `status` | OrderStatus | 주문 상태 |
| `orderedAt` | LocalDateTime | 주문 시각 |

---

### 주문 상태 (OrderStatus)
_Enum_

| 값 | 설명 |
|----|------|
| `PAYMENT_WAITING` | 결제 대기 |
| `PREPARING` | 상품 준비 중 |
| `SHIPPED` | 배송 시작 |
| `DELIVERING` | 배송 중 |
| `DELIVERY_COMPLETED` | 배송 완료 |
| `CANCELED` | 주문 취소 |

---

### 주문 상품 목록 (OrderItems)
_Value Object / 일급 컬렉션_

#### 속성 & 규칙
- `items` : List\<OrderItem\>
- 주문 상품은 1개 이상이어야 한다
- 총 가격은 각 주문 항목의 총액 합산이다

---

### 주문 상품 (OrderItem)
_Entity_

#### 행위 & 규칙
- 주문 항목 생성 : `create(OrderItemCommand command, IdGenerator idGenerator)`
  - 생성 시 식별자(`id`)가 할당된다
  - 수량은 1개 이상이어야 한다
  - 가격은 0원보다 커야 한다
- 주문 항목 복원 : `restore(RestoreOrderItemCommand command)`

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | OrderItemId | 식별자 |
| `productId` | ProductId | 주문 대상 상품 (상품 애그리거트 참조) |
| `quantity` | int | 주문 수량 |
| `price` | Money (VO) | 주문 시점 단가 스냅샷 |

---

### 배송 정보 (ShippingInfo)
_Value Object_

#### 속성 & 규칙
- `receiver` : Receiver (VO)
- `address` : Address (VO)
- 수령인 정보는 null일 수 없다
- 주소 정보는 null일 수 없다
- 회원 배송지가 이후 변경되더라도 주문의 배송 정보는 스냅샷으로 유지된다

---

### 금액 (Money), 주소 (Address), 수령인 (Receiver)
_Value Object — `domain.common.vo` 참조_

→ [`domain/common/README.md`](../../../domain/common/README.md)

---

## 테스트 시나리오

### OrderItem

- [x] 주문 수량이 1 이상이면 정상 생성된다
- [x] 주문 수량이 0 이하이면 예외가 발생한다
- [x] 주문 상품 가격이 0원이면 예외가 발생한다

### ShippingInfo

- [x] 유효한 수령인과 주소로 배송정보를 생성할 수 있다
- [x] 수령인이 null이면 예외가 발생한다
- [x] 주소가 null이면 예외가 발생한다

### Order

#### 주문 생성
- [x] 유효한 정보로 주문을 생성하면 식별자가 할당된다
- [x] 주문을 생성하면 `PAYMENT_WAITING` 상태가 된다
- [x] 주문을 생성하면 주문 시간이 기록된다
- [x] 주문을 생성하면 주문자 정보가 저장된다
- [x] 주문을 생성하면 배송지 정보가 스냅샷으로 저장된다
- [x] 주문을 생성하면 주문 상품 목록이 저장된다
- [x] 주문을 생성하면 각 주문 상품에 식별자가 할당된다
- [x] 주문을 생성하면 총 가격은 주문 상품들의 단가와 수량의 합산이다
- [x] 주문 상품이 없으면 예외가 발생한다
- [x] 주문 상품 수량이 0 이하이면 예외가 발생한다
- [x] 주문 상품 가격이 0원이면 예외가 발생한다

#### 주문 취소
- [x] `PAYMENT_WAITING` 상태에서 주문을 취소하면 `CANCELED` 상태가 된다
- [x] `PREPARING` 상태에서 주문을 취소할 수 있다
- [x] `SHIPPED` 상태에서 주문을 취소하면 예외가 발생한다
- [x] `DELIVERING` 상태에서 주문을 취소하면 예외가 발생한다
- [x] `DELIVERY_COMPLETED` 상태에서 주문을 취소하면 예외가 발생한다
- [x] 이미 취소된 주문은 다시 취소할 수 없다
