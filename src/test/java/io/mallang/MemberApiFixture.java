package io.mallang;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.MemberFixture.generateRegisterShippingAddressRequest;

public final class MemberApiFixture extends ApiFixture {

    public MemberApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> registerMember(MemberCreateRequest request) {
        return client().postForEntity("/members", request, Void.class);
    }

    public ResponseEntity<Void> registerShippingAddress(RegisterShippingAddressRequest request) {
        return client().postForEntity("/my/shipping-addresses", request, Void.class);
    }

    public ResponseEntity<Void> makeDefaultShippingAddress(String shippingAddressId) {
        return client().exchange(
                RequestEntity.patch("/my/shipping-addresses/" + shippingAddressId + "/default").build(),
                Void.class
        );
    }

    public ResponseEntity<Void> updateShippingAddress(String shippingAddressId, UpdateShippingAddressRequest request) {
        return client().exchange(
                RequestEntity.put("/my/shipping-addresses/" + shippingAddressId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeShippingAddress(String shippingAddressId) {
        return client().exchange(
                RequestEntity.delete("/my/shipping-addresses/" + shippingAddressId).build(),
                Void.class
        );
    }

    public String registerShippingAddressThenGetId() {
        ResponseEntity<Void> response = registerShippingAddress(generateRegisterShippingAddressRequest());
        return response.getHeaders().getLocation().getPath().substring("/my/shipping-addresses/".length());
    }
}
