# Stock 도메인

## 도메인 모델

### 재고 (Stock)
_Aggregate Root_

#### 행위 & 규칙
- 재고 생성 : `static create(CreateStockCommand command)`
  - 상품 식별자(`productId`)를 식별자로 사용한다
  - 수량은 0 이상이어야 한다
- 재고 복원 : `static restore(RestoreStockCommand command)`
- 재고 추가 : `add(quantity)`
  - 추가 수량은 0보다 커야 한다
- 재고 차감 : `deduct(quantity)`
  - 차감 수량은 0보다 커야 한다
  - 보유 재고보다 많은 수량을 차감할 수 없다
- 재고 확인 : `checkAvailable(quantity)`
  - 확인 수량은 0보다 커야 한다
  - 보유 재고보다 많은 수량이면 예외가 발생한다

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `productId` | ProductId | 상품 식별자이자 재고 식별자 |
| `quantity` | StockQuantity (VO) | 재고 수량 |

---

### 재고 수량 (StockQuantity)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | int | null 불가 / 음수 불가 / 0 허용 |

#### 캡슐화
- `StockQuantity`의 수량 연산은 `stock.domain` 패키지 내부에서만 사용한다.
- 외부에서는 `Stock`의 공개 행위인 `add`, `deduct`, `checkAvailable`을 통해 재고를 변경하거나 확인한다.

---

## 테스트 시나리오

### Stock

#### 생성
- [x] 수량은 null 또는 음수가 아니어야 한다
- [x] 수량이 0 이상이면 정상 생성된다

#### 재고 추가
- [x] 재고를 추가하면 수량이 증가한다
- [x] 추가 수량이 0 이하이면 예외가 발생한다

#### 재고 차감
- [x] 재고를 차감하면 수량이 감소한다
- [x] 차감 수량이 0 이하이면 예외가 발생한다
- [x] 차감 결과가 음수이면 예외가 발생한다

#### 재고 확인
- [x] 확인 수량이 0 이하이면 예외가 발생한다
- [x] 확인 대상 수량이 보유 재고보다 많으면 예외가 발생한다
- [x] 충분한 재고가 있으면 예외가 발생하지 않는다
