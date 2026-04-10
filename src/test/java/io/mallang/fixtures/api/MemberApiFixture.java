package io.mallang.fixtures.api;

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
        return client().postForEntity(MEMBERS_API, request, Void.class);
    }

    public String registerMemberThenGetId(MemberCreateRequest request) {
        ResponseEntity<Void> response = registerMember(request);

        return extractId(response);
    }

    public ResponseEntity<Void> registerShippingAddress(RegisterShippingAddressRequest request) {
        return client().postForEntity(SHIPPING_ADDRESSES_API, request, Void.class);
    }

    public ResponseEntity<Void> makeDefaultShippingAddress(String shippingAddressId) {
        return client().exchange(
                RequestEntity.patch(SHIPPING_ADDRESSES_API + "/" + shippingAddressId + "/default").build(),
                Void.class
        );
    }

    public ResponseEntity<Void> updateShippingAddress(String shippingAddressId, UpdateShippingAddressRequest request) {
        return client().exchange(
                RequestEntity.put(SHIPPING_ADDRESSES_API + "/" + shippingAddressId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeShippingAddress(String shippingAddressId) {
        return client().exchange(
                RequestEntity.delete(SHIPPING_ADDRESSES_API + "/" + shippingAddressId).build(),
                Void.class
        );
    }

    public String registerShippingAddressThenGetId() {
        ResponseEntity<Void> response = registerShippingAddress(generateRegisterShippingAddressRequest());

        return extractId(response);
    }
}
