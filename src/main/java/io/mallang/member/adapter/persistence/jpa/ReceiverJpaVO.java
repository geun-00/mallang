package io.mallang.member.adapter.persistence.jpa;

import io.mallang.domain.common.Receiver;
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
