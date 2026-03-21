package io.mallang.member.adapter.web;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.domain.MemberCreateCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemberCommandApi {

    private final RegisterMemberUseCase registerMemberUseCase;

    @PostMapping("/members")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberCreateRequest createRequest) {
        RegisterMemberResult result = registerMemberUseCase.register(new MemberCreateCommand(createRequest.email(), createRequest.password(), createRequest.nickname()));

        return ResponseEntity.created(URI.create("/members/" + result.memberId())).build();
    }
}
