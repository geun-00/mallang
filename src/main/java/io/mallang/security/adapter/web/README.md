# API 목록

* [로그인](#로그인)
* [로그아웃](#로그아웃)

---

### 로그인

#### 요청
- `POST /api/v1/login`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/api/v1/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "member1@example.com",
    "password": "password12@"
  }'
  ```

#### 성공 응답
- 상태코드: `200 OK`
- 헤더
  ```
  Set-Cookie: JSESSIONID=...
  ```
- 본문 예시
  ```json
  {
    "csrfToken": "csrf-token"
  }
  ```

#### 실패 응답
- 상태코드:
  - `400 Bad Request`
  - `401 Unauthorized`

#### 정책
- 인증 없이 요청할 수 있다
- `email`은 필수다
- `password`는 필수다
- 이메일 또는 비밀번호가 올바르지 않으면 인증에 실패한다
- 로그인에 성공하면 세션 쿠키와 CSRF 토큰을 발급한다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `200 OK` 상태코드를 반환한다
- [x] 올바르게 요청하면 세션 쿠키와 CSRF 토큰을 반환한다
- [x] 올바르게 로그인하면 반환된 세션 쿠키와 CSRF 토큰으로 보호된 API에 접근할 수 있다
- [x] 비밀번호가 올바르지 않으면 `401 Unauthorized` 상태코드를 반환한다
- [x] `email` 속성이 비어있으면 `400 Bad Request` 상태코드를 반환한다
- [x] `password` 속성이 비어있으면 `400 Bad Request` 상태코드를 반환한다

---

### 로그아웃

#### 요청
- `POST /api/v1/logout`
- curl 명령 예시
  ```bash
  curl -i -X POST 'http://localhost:8080/api/v1/logout' \
  -H 'Cookie: JSESSIONID=...' \
  -H 'X-CSRF-TOKEN: csrf-token'
  ```

#### 성공 응답
- 상태코드: `204 No Content`

#### 실패 응답
- 상태코드:
  - `403 Forbidden`

#### 정책
- 인증 없이 요청할 수 있다
- CSRF 토큰이 필요하다
- 인증된 상태에서 로그아웃하면 인증 세션을 무효화한다
- 인증되지 않은 상태에서 요청하더라도 CSRF 검증을 통과하면 `204 No Content`를 반환한다

#### 테스트 시나리오
- [x] 올바르게 요청하면 `204 No Content` 상태코드를 반환한다
- [x] 로그아웃 후에는 같은 세션 쿠키와 CSRF 토큰으로 보호된 API에 접근할 수 없다
