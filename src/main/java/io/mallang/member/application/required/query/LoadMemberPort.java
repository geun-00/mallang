package io.mallang.member.application.required.query;

import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.exception.MemberNotFoundException;

public interface LoadMemberPort {

    /**
     * @throws MemberNotFoundException
     */
    Member getById(MemberId memberId);

    /**
     * @throws MemberNotFoundException
     */
    Member getByEmail(Email email);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
