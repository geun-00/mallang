package io.mallang.order.adapter.persistence.jpa;

import io.mallang.common.domain.vo.Receiver;
import jakarta.persistence.Embeddable;

@Embeddable
public record ReceiverJpaVO(String name, String phoneNumber) {

    public static ReceiverJpaVO from(Receiver receiver) {
        return new ReceiverJpaVO(receiver.name(), receiver.phoneNumber());
    }

    public Receiver toDomain() {
        return new Receiver(name, phoneNumber);
    }
}
