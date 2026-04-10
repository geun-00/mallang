package io.mallang.member.application.event;

import io.mallang.member.domain.MemberId;

public record MemberRegisteredEvent(MemberId memberId) {
}
