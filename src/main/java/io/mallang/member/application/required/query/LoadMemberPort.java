package io.mallang.member.application.required.query;

import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.Nickname;

public interface LoadMemberPort {

    /**
     * @throws io.mallang.member.domain.MemberNotFoundException
     */
    Member getById(MemberId memberId);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
