package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;

public interface RegisterMemberUseCase {

    RegisterMemberResult register(RegisterMemberCommand command);
}
