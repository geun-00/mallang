package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;

public interface RegisterMemberUseCase {

    RegisterMemberResult register(RegisterMemberCommand command);
}
