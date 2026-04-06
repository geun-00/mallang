# mallang

이 프로젝트의 주요 목표는 DDD와 TDD를 학습하는 것입니다. 주문과 상품을 비롯한 커머스 도메인을 예시로 삼아, 도메인 모델링과 테스트 중심 개발을 함께 연습합니다.

## 문서

### 도메인 문서

- [Member](./src/main/java/io/mallang/member/README.md)
- [Product](./src/main/java/io/mallang/product/README.md)
- [Cart](./src/main/java/io/mallang/cart/README.md)
- [Order](./src/main/java/io/mallang/order/README.md)
- [Domain Common](./src/main/java/io/mallang/domain/common/README.md)

### API 문서

- [Member API](./src/main/java/io/mallang/member/adapter/web/README.md)
- [Product API](./src/main/java/io/mallang/product/adapter/web/README.md)
- [Cart API](./src/main/java/io/mallang/cart/adapter/web/README.md)
- [Order API](./src/main/java/io/mallang/order/adapter/web/README.md)

## 프로젝트 구조

```text
io.mallang.{domain}
├── domain        # 애그리거트, 엔티티, 값 객체, 도메인 규칙
├── application
│   ├── provided
│   │   ├── command  # 명령 유스케이스 진입 포트
│   │   └── query    # 조회 유스케이스 진입 포트
│   ├── required
│   │   ├── command  # 명령 유스케이스에서 사용하는 외부 의존 포트
│   │   └── query    # 조회 유스케이스에서 사용하는 외부 의존 포트
│   └── service
│       ├── command  # 명령 유스케이스 구현
│       └── query    # 조회 유스케이스 구현
└── adapter
    ├── web
    │   ├── command  # 명령 API 진입 어댑터
    │   └── query    # 조회 API 진입 어댑터
    ├── persistence
    │   ├── command  # 명령 포트를 구현하는 영속성 어댑터
    │   └── query    # 조회 포트를 구현하는 영속성 어댑터
    └── security    # 인증/인가 관련 구현(Member)
```

## 다이어그램

### 전체 구조

```mermaid
flowchart LR
    Client["Client / Test"]

    subgraph LeftAdapter["Driving Adapters"]
        Web["Web Adapter"]
        Security["Security Adapter"]
    end

    subgraph Hexagon["Hexagonal Core"]
        direction TB

        subgraph CommandSide["Command"]
            direction LR
            CommandIn["Command Ports"]
            CommandService["Command Services"]
            Domain["Domain Model"]
            CommandOut["Command Ports"]

            CommandIn --> CommandService
            CommandService --> Domain
            Domain --> CommandOut
        end

        subgraph QuerySide["Query"]
            direction LR
            QueryIn["Query Ports"]
            QueryService["Query Services"]
            QueryModel["Query Model"]
            QueryOut["Query Ports"]

            QueryIn --> QueryService
            QueryService --> QueryModel
            QueryModel --> QueryOut
        end
    end

    subgraph RightAdapter["Driven Adapters"]
        CommandPersistence["Command Persistence Adapter"]
        QueryPersistence["Query Persistence Adapter"]
    end

    Client --> Web
    Web --> CommandIn
    Web --> QueryIn
    Security --> Web
    CommandOut --> CommandPersistence
    QueryOut --> QueryPersistence

    class Client external
    class Web,Security driving
    class CommandIn,CommandService,Domain,CommandOut core
    class QueryIn,QueryService,QueryModel,QueryOut query
    class CommandPersistence,QueryPersistence driven
    classDef external fill:#f5f1e8,stroke:#6b5b3e,stroke-width:1.5px,color:#2f2417;
    classDef driving fill:#dbeafe,stroke:#1d4ed8,stroke-width:1.5px,color:#0f172a;
    classDef core fill:#dcfce7,stroke:#15803d,stroke-width:1.5px,color:#14532d;
    classDef query fill:#fef3c7,stroke:#d97706,stroke-width:1.5px,color:#451a03;
    classDef driven fill:#fee2e2,stroke:#dc2626,stroke-width:1.5px,color:#4c0519;
```

### 도메인 관계

```mermaid
flowchart TB
    Common["Domain Common\nMoney / Address / Receiver"]

    subgraph AggregateRow1[" "]
        direction LR
    subgraph MemberAggregate["Member Aggregate"]
        direction TB
        MemberRoot["Member (Root)"]
        MemberParts["Email / Nickname / Password / ShippingAddress"]
        MemberRoot --> MemberParts
    end

    subgraph ProductAggregate["Product Aggregate"]
        direction TB
        ProductRoot["Product (Root)"]
        ProductParts["ProductName / ProductDescription / ProductImage / StockQuantity"]
        ProductRoot --> ProductParts
    end
    end

    subgraph AggregateRow2[" "]
        direction LR
    subgraph CartAggregate["Cart Aggregate"]
        direction TB
        CartRoot["Cart (Root)"]
        CartParts["CartItem"]
        CartRoot --> CartParts
    end

    subgraph OrderAggregate["Order Aggregate"]
        direction TB
        OrderRoot["Order (Root)"]
        OrderParts["OrderItem / ShippingInfo"]
        OrderRoot --> OrderParts
    end
    end

    MemberRoot --> Common
    ProductRoot --> Common
    OrderRoot --> Common
    ProductRoot --> MemberRoot
    CartRoot --> MemberRoot
    CartRoot --> ProductRoot
    OrderRoot --> MemberRoot
    OrderRoot ---> ProductRoot

    class MemberRoot,ProductRoot,CartRoot,OrderRoot root
    class MemberParts,ProductParts,CartParts,OrderParts inner
    class Common common
    style AggregateRow1 fill:transparent,stroke:transparent
    style AggregateRow2 fill:transparent,stroke:transparent
    classDef root fill:#dcfce7,stroke:#15803d,stroke-width:1.5px,color:#14532d;
    classDef inner fill:#f0fdf4,stroke:#65a30d,stroke-width:1px,color:#365314;
    classDef common fill:#fef3c7,stroke:#d97706,stroke-width:1.5px,color:#451a03;
```
