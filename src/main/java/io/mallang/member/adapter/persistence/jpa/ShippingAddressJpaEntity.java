package io.mallang.member.adapter.persistence.jpa;

import io.mallang.common.adapter.persistence.jpa.BaseEntity;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.command.RestoreShippingAddressCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ShippingAddressJpaEntity extends BaseEntity {

    @Id
    @Column(name = "shipping_address_id")
    private String id;

    @Embedded
    private ReceiverJpaVO receiver;

    @Embedded
    private AddressJpaVO address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberJpaEntity member;

    private boolean isDefault;

    private ShippingAddressJpaEntity(
            String id,
            ReceiverJpaVO receiver,
            AddressJpaVO address,
            MemberJpaEntity member,
            boolean isDefault
    ) {
        this.id = id;
        this.receiver = receiver;
        this.address = address;
        this.member = member;
        this.isDefault = isDefault;
    }

    static ShippingAddressJpaEntity from(ShippingAddress shippingAddress, MemberJpaEntity member) {
        return new ShippingAddressJpaEntity(
                shippingAddress.getId().value(),
                ReceiverJpaVO.from(shippingAddress.getReceiver()),
                AddressJpaVO.from(shippingAddress.getAddress()),
                member,
                shippingAddress.isDefault()
        );
    }

    ShippingAddress toDomain() {
        return ShippingAddress.restore(new RestoreShippingAddressCommand(
                new ShippingAddressId(id),
                receiver.toDomain(),
                address.toDomain(),
                isDefault
        ));
    }

    void updateFrom(ShippingAddress shippingAddress) {
        this.receiver = ReceiverJpaVO.from(shippingAddress.getReceiver());
        this.address = AddressJpaVO.from(shippingAddress.getAddress());
        this.isDefault = shippingAddress.isDefault();
    }

    String getId() {
        return id;
    }
}
