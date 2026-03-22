# Member 도메인

---

## 도메인 모델

### 회원 (Member)
_Aggregate Root_

#### 행위 & 규칙
- 회원 생성 : `static create(CreateMemberCommand command, PasswordEncoder encoder, IdGenerator idGenerator, ClockHolder clockHolder)`
  - 생성 시 상태는 `ACTIVE`
  - 생성 시 식별자(`id`) 할당
  - 가입 시간(`joinedAt`) 기록
- 회원 탈퇴 : `withdraw(ClockHolder clockHolder)`
  - `ACTIVE` 상태인 회원만 탈퇴할 수 있다
  - 탈퇴 시 상태는 `WITHDRAWN`, 탈퇴 시간(`withdrawnAt`) 기록
- 주문 가능 여부 확인 : `isOrderable() : boolean`
  - `ACTIVE` 상태인 회원만 주문할 수 있다
- 배송지 추가 : `addShippingAddress(AddShippingAddressCommand command, IdGenerator idGenerator)`
  - `ACTIVE` 상태인 회원만 배송지를 추가할 수 있다
  - 첫 번째로 추가되는 배송지는 자동으로 기본 배송지가 된다
- 기본 배송지 설정 : `setDefaultShippingAddress(ShippingAddressId shippingAddressId)`
  - `ACTIVE` 상태인 회원만 기본 배송지를 변경할 수 있다
  - 기존 기본 배송지는 해제된다
  - 본인의 배송지만 기본으로 설정할 수 있다
- 배송지 수정 : `modifyShippingAddress(ShippingAddressId id, ModifyShippingAddressCommand command)`
  - `ACTIVE` 상태인 회원만 배송지를 수정할 수 있다
  - 본인의 배송지만 수정할 수 있다
- 배송지 삭제 : `removeShippingAddress(ShippingAddressId id)`
  - `ACTIVE` 상태인 회원만 배송지를 삭제할 수 있다
  - 본인의 배송지만 삭제할 수 있다

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | MemberId | 식별자 |
| `email` | Email (VO) | 이메일 |
| `nickname` | Nickname (VO) | 닉네임 |
| `password` | Password (VO) | 해싱된 비밀번호 |
| `status` | MemberStatus | 회원 상태 |
| `joinedAt` | LocalDateTime | 가입 시간 |
| `withdrawnAt` | LocalDateTime (nullable) | 탈퇴 시간 |
| `shippingAddresses` | ShippingAddresses | 배송지 목록 (일급 컬렉션) |

---

### 회원 상태 (MemberStatus)
_Enum_

| 값 | 설명 |
|----|------|
| `ACTIVE` | 등록 완료 |
| `WITHDRAWN` | 탈퇴 |

---

### 배송지 (ShippingAddress)
_Entity_

#### 속성
| 속성 | 타입 | 설명 |
|------|------|------|
| `id` | ShippingAddressId | 식별자 |
| `receiver` | Receiver (VO) | 수신자 정보 |
| `address` | Address (VO) | 주소 정보 |
| `isDefault` | boolean | 기본 배송지 여부 |

---

### 주소 (Address), 수신자 (Receiver)
_Value Object — `domain.common.vo` 참조_

→ [`domain/common/README.md`](../../../domain/common/README.md)

---

### 닉네임 (Nickname)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | String | null·공백 불가 / 2자 이상 20자 이하 / 허용되지 않는 특수문자 포함 불가 |

---

### 이메일 (Email)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `address` | String | 이메일 형식(`xxx@xxx.xx`) |

---

### 비밀번호 (Password)
_Value Object_

#### 속성 & 규칙
| 속성 | 타입 | 규칙 |
|------|------|------|
| `value` | String | 해싱된 값으로 저장 |

- 정적 생성자 `Password.encode(rawPassword, encoder)`
  - 8자 이상 20자 이하여야 한다
  - 영문, 숫자, 특수문자(`!@#$%^&*`)를 각각 포함해야 한다
  - 허용된 문자(`영문`, `숫자`, `!@#$%^&*`) 외 문자는 포함할 수 없다
  - 검증 통과 후 `PasswordEncoder`로 해싱하여 생성

---

## 테스트 시나리오

### Nickname

- [x] 유효한 형식으로 닉네임으로 생성할 수 있다
- [x] 닉네임은 null 또는 공백이 아니어야 한다
- [x] 닉네임은 앞뒤 공백이 없어야 한다
- [x] 닉네임은 2자 이상 20자 이하여야 한다
- [x] 닉네임은 허용된 특수문자만 포함할 수 있다

### Email

- [x] 유효한 이메일 형식으로 생성할 수 있다
- [x] 이메일은 null이나 빈 문자열이 될 수 없다
- [x] 유효하지 않은 형식으로 생성할 수 없다

### Receiver

→ [`domain/common/README.md`](../../../domain/common/README.md) 참조

### Member

#### 회원 생성
- [x] 유효한 정보로 회원을 생성하면 ACTIVE 상태가 된다
- [x] 회원을 생성하면 식별자가 할당된다
- [x] 회원을 생성하면 커맨드의 정보가 저장된다
- [x] 회원을 생성하면 가입 시간이 기록된다
- [x] 회원을 생성하면 비밀번호가 해싱되어 저장된다
- [x] 비밀번호는 8자 이상 20자 이하여야 한다
- [x] 비밀번호는 영문, 숫자, 특수문자를 포함해야 한다
- [x] 비밀번호는 허용된 문자로만 구성되어야 한다

#### 회원 탈퇴
- [x] 탈퇴 시 상태는 `WITHDRAWN`이 된다
- [x] 탈퇴 시 탈퇴 시간이 기록된다
- [x] 이미 탈퇴한 회원은 다시 탈퇴할 수 없다

#### 주문 가능 여부 확인
- [x] `ACTIVE` 회원은 주문 할 수 있다
- [x] 탈퇴한 회원은 주문 할 수 없다

### ShippingAddress

#### 배송지 추가
- [x] 배송지를 추가할 수 있다
- [x] 배송지를 추가하면 수신인, 주소 정보, 식별자가 생성되어 저장된다
- [x] 처음 추가한 배송지는 자동으로 기본 배송지가 된다
- [x] 두 번째 추가한 배송지는 기본 배송지가 되지 않는다
- [x] 배송지는 최대 5개까지 추가할 수 있다
- [x] `WITHDRAWN` 회원은 배송지를 추가할 수 없다

#### 기본 배송지 설정
- [x] 기본 배송지를 변경하면 기존 기본 배송지는 해제된다
- [x] 이미 기본 배송지인 배송지를 다시 기본으로 설정해도 정상 처리된다
- [x] `WITHDRAWN` 회원은 기본 배송지를 설정할 수 없다
- [x] 본인 배송지가 아니면 기본 배송지로 설정할 수 없다

#### 배송지 수정
- [x] 배송지를 수정할 수 있다
- [x] 배송지를 수정해도 기본 배송지 여부는 변경되지 않는다
- [x] `WITHDRAWN` 회원은 배송지를 수정할 수 없다
- [x] 본인 배송지가 아니면 수정할 수 없다

#### 배송지 삭제
- [x] 배송지를 삭제할 수 있다
- [x] `WITHDRAWN` 회원은 배송지를 삭제할 수 없다
- [x] 본인 배송지가 아니면 삭제할 수 없다
