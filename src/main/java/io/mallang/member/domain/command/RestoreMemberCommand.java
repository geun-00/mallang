package io.mallang.member.domain.command;

import io.mallang.member.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public record RestoreMemberCommand(
        MemberId id,
        Email email,
        Nickname nickname,
        Password password,
        LocalDateTime joinedAt,
        MemberStatus status,
        LocalDateTime withdrawnAt,
        List<ShippingAddress> shippingAddresses
) {
}
