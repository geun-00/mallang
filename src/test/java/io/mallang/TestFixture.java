package io.mallang;

import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.adapter.web.model.AddStockRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.DeductStockRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

@Deprecated(forRemoval = false)
public final class TestFixture {

    private final AuthFixture authFixture;
    private final CartApiFixture cartFixture;
    private final OrderApiFixture orderFixture;
    private final MemberApiFixture memberFixture;
    private final ProductApiFixture productFixture;

    private TestFixture(FixtureContext context) {
        this.authFixture = new AuthFixture(context);
        this.memberFixture = new MemberApiFixture(context);
        this.productFixture = new ProductApiFixture(context);
        this.cartFixture = new CartApiFixture(context);
        this.orderFixture = new OrderApiFixture(context);
    }

    public static TestFixture create(Environment environment) {
        return new TestFixture(new FixtureContext(environment));
    }

    public void createMemberThenLogin() {
        authFixture.createMemberThenLogin();
    }

    public void login(String email, String password) {
        authFixture.login(email, password);
    }

    public TestRestTemplate client() {
        return memberFixture.client();
    }

    public TestRestTemplate unauthenticatedClient() {
        return memberFixture.unauthenticatedClient();
    }

    public ResponseEntity<Void> registerMember(MemberCreateRequest request) {
        return memberFixture.registerMember(request);
    }

    public ResponseEntity<Void> registerShippingAddress(RegisterShippingAddressRequest request) {
        return memberFixture.registerShippingAddress(request);
    }

    public ResponseEntity<Void> updateShippingAddress(String shippingAddressId, UpdateShippingAddressRequest request) {
        return memberFixture.updateShippingAddress(shippingAddressId, request);
    }

    public ResponseEntity<Void> removeShippingAddress(String shippingAddressId) {
        return memberFixture.removeShippingAddress(shippingAddressId);
    }

    public String registerShippingAddressThenGetId() {
        return memberFixture.registerShippingAddressThenGetId();
    }

    public ResponseEntity<Void> addCartItem(AddCartItemRequest request) {
        return cartFixture.addCartItem(request);
    }

    public String addCartItemThenGetId(String productId, int quantity) {
        return cartFixture.addCartItemThenGetId(productId, quantity);
    }

    public ResponseEntity<Void> changeCartItemQuantity(String cartItemId, ChangeCartItemQuantityRequest request) {
        return cartFixture.changeCartItemQuantity(cartItemId, request);
    }

    public ResponseEntity<Void> removeCartItem(String cartItemId) {
        return cartFixture.removeCartItem(cartItemId);
    }

    public ResponseEntity<Void> clearCart() {
        return cartFixture.clearCart();
    }

    public ResponseEntity<Void> registerProduct(CreateProductRequest request) {
        return productFixture.registerProduct(request);
    }

    public ResponseEntity<Void> updateProduct(String productId, UpdateProductRequest request) {
        return productFixture.updateProduct(productId, request);
    }

    public ResponseEntity<Void> addStock(String productId, AddStockRequest request) {
        return productFixture.addStock(productId, request);
    }

    public ResponseEntity<Void> deductStock(String productId, DeductStockRequest request) {
        return productFixture.deductStock(productId, request);
    }

    public ResponseEntity<Void> discontinue(String productId) {
        return productFixture.discontinue(productId);
    }

    public ResponseEntity<Void> addImages(String productId, AddProductImagesRequest request) {
        return productFixture.addImages(productId, request);
    }

    public ResponseEntity<Void> removeImage(String productId, String imageId) {
        return productFixture.removeImage(productId, imageId);
    }

    public ResponseEntity<Void> changeThumbnailImage(String productId, String imageId) {
        return productFixture.changeThumbnailImage(productId, imageId);
    }

    public String registerProductThenGetId() {
        return productFixture.registerProductThenGetId();
    }

    public ResponseEntity<Void> createOrder(CreateOrderRequest request) {
        return orderFixture.createOrder(request);
    }

    public ResponseEntity<Void> cancelOrder(String orderId) {
        return orderFixture.cancelOrder(orderId);
    }

    public String createOrderThenGetId(CreateOrderRequest request) {
        return orderFixture.createOrderThenGetId(request);
    }
}
