package io.mallang.init;

import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Profile("local")
@RequiredArgsConstructor
public class MemberSeeder {

    private static final int MEMBER_COUNT = 3;
    private static final String SEED_MEMBER_PASSWORD = "password12@";

    private final LoadMemberPort loadMemberPort;
    private final RegisterMemberUseCase registerMemberUseCase;

    boolean isAlreadySeeded() {
        return loadMemberPort.existsByEmail(new Email(seedMemberEmail(1)));
    }

    List<String> seed() {
        return IntStream.rangeClosed(1, MEMBER_COUNT)
                        .mapToObj(this::registerMember)
                        .toList();
    }

    private String registerMember(int seedNumber) {
        return registerMemberUseCase.register(new RegisterMemberCommand(
                                            seedMemberEmail(seedNumber),
                                            SEED_MEMBER_PASSWORD,
                                            seedMemberNickname(seedNumber)
                                    ))
                                    .memberId();
    }

    private String seedMemberEmail(int seedNumber) {
        return "seed-member%03d@test.com".formatted(seedNumber);
    }

    private String seedMemberNickname(int seedNumber) {
        return "테스트회원%03d".formatted(seedNumber);
    }
}
