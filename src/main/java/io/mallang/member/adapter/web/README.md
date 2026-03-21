## API 목록

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

성공 응답
- 상태코드: 201 Created
- 헤더
  ```
  Location: /members/{memberId}
  ```

실패 응답
- 상태코드: `400 Bad Request` or `409 Conflict`

정책
- email은 필수다
- password는 필수다
- nickname은 필수다
- 이메일은 유일해야 한다
- 닉네임은 유일해야 한다

테스트 시나리오
- [x] 올바르게 요청하면 `201 Created` 상태코드를 반환한다
- [x] 올바르게 요청하면 식별자가 포함된 `Location` 헤더를 반환한다
- [x] 올바르게 요청하면 `Location` 헤더의 식별자로 회원을 조회할 수 있다
- [x] email 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] password 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] nickname 속성이 지정되지 않으면 `400 Bad Request` 상태코드를 반환한다
- [x] 이미 존재하는 이메일이면 `409 Conflict` 상태코드를 반환한다
- [x] 이미 존재하는 닉네임이면 `409 Conflict` 상태코드를 반환한다
- [x] 도메인 규칙을 위반하면 `400 Bad Request` 상태코드를 반환한다
