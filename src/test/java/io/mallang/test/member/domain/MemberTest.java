package io.mallang.test.member.domain;

import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberCreateCommand;
import io.mallang.member.domain.MemberStatus;
import org.junit.jupiter.api.Test;

import static io.mallang.fixtures.MemberFixture.generateCreateCommand;
import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.MemberFixture.generatePasswordEncoder;
import static io.mallang.member.domain.Member.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    void 유효한_정보로_회원을_생성하면_ACTIVE_상태가_된다() {
        // given
        MemberCreateCommand createCommand = generateCreateCommand();

        // when
        Member member = create(createCommand, generatePasswordEncoder());

        // then
        assertThat(member.isActive()).isTrue();
    }

    @Test
    void 회원을_생성하면_가입_시간이_기록된다() {
        // given
        Member member = generateMember();

        // when
        // then
        assertThat(member.getJoinedAt()).isNotNull();
    }

    @Test
    void 비밀번호는_8자_이상_20자_이하여야_한다() {
        assertThatThrownBy(() -> generateMember("1".repeat(7))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> generateMember("1".repeat(21))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호는_영문_숫자_특수문자를_포함해야_한다() {
        assertThatThrownBy(() -> generateMember("ㄱㄴㄷㄹ12@")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> generateMember("qwerwasd@")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> generateMember("qwerwasd12")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호는_허용된_문자로만_구성되어야_한다() {
        assertThatThrownBy(() -> generateMember("(qwerwasd12@")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> generateMember("qwerwasd12@)")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> generateMember("qwerwasd12@★")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 회원을_생성하면_비밀번호가_해싱되어_저장된다() {
        // given
        MemberCreateCommand createCommand = generateCreateCommand();
        String rawPassword = createCommand.password();

        // when
        Member member = create(createCommand, generatePasswordEncoder());

        // then
        assertThat(member.getPassword().value()).isNotEqualTo(rawPassword);
    }

    @Test
    void 탈퇴_시_상태는_WITHDRAWN이_된다() {
        // given
        Member member = generateMember();

        // when
        member.withdraw();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }
    
    @Test
    void 탈퇴_시_탈퇴_시간이_기록된다() {
        // given
        Member member = generateMember();
        assertThat(member.getWithdrawnAt()).isNull();
        
        // when
        member.withdraw();
        
        // then
        assertThat(member.getWithdrawnAt()).isNotNull();
    }
    
    @Test
    void 이미_탈퇴한_회원은_다시_탈퇴할_수_없다() {
        // given
        Member member = generateMember();
        member.withdraw();

        // when
        // then
        assertThatThrownBy(member::withdraw)
                .isInstanceOf(IllegalStateException.class);
    }
    
    @Test
    void ACTIVE_회원은_주문_할_수_있다() {
        // given
        Member member = generateMember();

        // when
        // then
        assertThat(member.isOrderable()).isTrue();
    }

    @Test
    void 탈퇴한_회원은_주문할_수_없다() {
        // given
        Member member = generateMember();
        member.withdraw();

        // when
        // then
        assertThat(member.isOrderable()).isFalse();
    }
}