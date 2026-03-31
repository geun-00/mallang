package io.mallang.member.adapter.persistence.jpa;

import io.mallang.domain.common.vo.Receiver;
import jakarta.persistence.Embeddable;

@Embeddable
record ReceiverJpaVO(String name, String phoneNumber) {

    static ReceiverJpaVO from(Receiver receiver) {
        return new ReceiverJpaVO(receiver.name(), receiver.phoneNumber());
    }

    Receiver toDomain() {
        return new Receiver(name, phoneNumber);
    }
}
