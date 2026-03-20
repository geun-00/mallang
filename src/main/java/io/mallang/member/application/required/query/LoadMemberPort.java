package io.mallang.member.application.required.query;

import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;

public interface LoadMemberPort {

    /**
     * @throws io.mallang.member.domain.MemberNotFoundException
     */
    Member getById(MemberId memberId);
}
