package io.mallang.member.application.required.command;

import io.mallang.member.domain.Member;

public interface SaveMemberPort {

    void save(Member member);
}
