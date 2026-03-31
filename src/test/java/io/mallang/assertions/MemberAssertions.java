package io.mallang.assertions;

import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.member.application.provided.command.model.UpdateShippingAddressCommand;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberPasswordEncoder;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.domain.command.RestoreMemberCommand;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberAssertions {

    public static ThrowingConsumer<Member> isDerivedFrom(CreateMemberCommand command, MemberPasswordEncoder passwordEncoder) {
        return member -> {
            assertThat(member.getEmail()).isEqualTo(command.email());
            assertThat(member.getNickname()).isEqualTo(command.nickname());
            assertThat(member.verifyPassword(command.password(), passwordEncoder)).isTrue();
        };
    }

    public static ThrowingConsumer<Member> isSameAs(Member expected) {
        return actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
            assertThat(actual.getNickname()).isEqualTo(expected.getNickname());
            assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
            assertThat(actual.getJoinedAt()).isEqualTo(expected.getJoinedAt());
        };
    }

    public static ThrowingConsumer<Member> isRestoredFrom(RestoreMemberCommand command) {
        return member -> {
            assertThat(member.getId()).isEqualTo(command.id());
            assertThat(member.getEmail()).isEqualTo(command.email());
            assertThat(member.getNickname()).isEqualTo(command.nickname());
            assertThat(member.getPassword()).isEqualTo(command.password());
            assertThat(member.getJoinedAt()).isEqualTo(command.joinedAt());
            assertThat(member.getStatus()).isEqualTo(command.status());
            assertThat(member.getWithdrawnAt()).isEqualTo(command.withdrawnAt());
            assertThat(member.getShippingAddresses())
                    .map(ShippingAddress::getId)
                    .isEqualTo(command.shippingAddresses().stream()
                                      .map(ShippingAddress::getId)
                                      .toList());
        };
    }

    public static ThrowingConsumer<ShippingAddress> isModifiedBy(UpdateShippingAddressCommand command) {
        return shippingAddress -> {
            assertThat(shippingAddress.getReceiver()).isEqualTo(new Receiver(command.receiverName(), command.receiverPhoneNumber()));
            assertThat(shippingAddress.getAddress()).isEqualTo(new Address(command.zipCode(), command.mainAddress(), command.detailAddress()));
        };
    }
}
