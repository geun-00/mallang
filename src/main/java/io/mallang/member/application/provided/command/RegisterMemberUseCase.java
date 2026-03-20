package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.domain.MemberCreateCommand;

public interface RegisterMemberUseCase {

    RegisterMemberResult register(MemberCreateCommand createCommand);
}
