package io.mallang.fixtures.api;

public final class FixtureSession {

    private final AuthFixture authFixture;
    private final MemberApiFixture memberFixture;
    private final ProductApiFixture productFixture;
    private final CartApiFixture cartFixture;
    private final OrderApiFixture orderFixture;

    FixtureSession(FixtureContext context) {
        this.memberFixture = new MemberApiFixture(context);
        this.authFixture = new AuthFixture(context, memberFixture);
        this.productFixture = new ProductApiFixture(context);
        this.cartFixture = new CartApiFixture(context);
        this.orderFixture = new OrderApiFixture(context);
    }

    public AuthFixture auth() {
        return authFixture;
    }

    public MemberApiFixture member() {
        return memberFixture;
    }

    public ProductApiFixture product() {
        return productFixture;
    }

    public CartApiFixture cart() {
        return cartFixture;
    }

    public OrderApiFixture order() {
        return orderFixture;
    }
}
