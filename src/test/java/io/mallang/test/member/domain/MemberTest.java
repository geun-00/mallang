package io.mallang.test.member.domain;

import io.mallang.member.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.mallang.fixtures.MemberFixture.*;
import static io.mallang.member.domain.Member.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    void 유효한_정보로_회원을_생성하면_ACTIVE_상태가_된다() {
        // given
        MemberCreateCommand createCommand = generateCreateCommand();

        // when
        Member member = create(createCommand, generatePasswordEncoder(), generateIdGenerator());

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

    @ParameterizedTest
    @ValueSource(strings = {
            "short1@", // 7자
            "longpassword1234567890@", // 21자
    })
    void 비밀번호는_8자_이상_20자_이하여야_한다(String invalidPassword) {
        assertThatThrownBy(() -> generateMember(invalidPassword)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password",   // 영문만
            "12345678",   // 숫자만
            "@@@@@@@@",   // 특수문자만
            "password12", // 영문 + 숫자
            "password@",  // 영문 + 특수문자
            "12345678@",  // 숫자 + 특수문자
    })
    void 비밀번호는_영문_숫자_특수문자를_포함해야_한다(String invalidPassword) {
        assertThatThrownBy(() -> generateMember(invalidPassword)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(qwerwasd12@", // 괄호 포함
            "qwerwasd12@)", // 괄호 포함
            "qwerwasd12@★", // 기타 특수문자 포함
    })
    void 비밀번호는_허용된_문자로만_구성되어야_한다(String invalidPassword) {
        assertThatThrownBy(() -> generateMember(invalidPassword)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void 식별자에_null이나_공백이_할당될_수_없다(String invalidId) {
        assertThatThrownBy(() -> create(generateCreateCommand(), generatePasswordEncoder(), () -> invalidId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 회원이_생성되면_식별자가_할당된다() {
        // given
        Member member = create(generateCreateCommand(), generatePasswordEncoder(), generateIdGenerator());

        // when
        // then
        assertThat(member.getId()).isNotNull();
        assertThat(member.getId().value()).isNotNull();
    }

    @Test
    void 회원을_생성하면_비밀번호가_해싱되어_저장된다() {
        // given
        MemberCreateCommand createCommand = generateCreateCommand();
        String rawPassword = createCommand.password();

        // when
        Member member = create(createCommand, generatePasswordEncoder(), generateIdGenerator());

        // then
        assertThat(member.getPassword().value()).isNotEqualTo(rawPassword);
    }

    @Test
    void 탈퇴_시_상태는_WITHDRAWN이_된다() {
        // given
        Member member = generateWithdrawnMember();

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
        Member member = generateWithdrawnMember();

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
        Member member = generateWithdrawnMember();

        // when
        // then
        assertThat(member.isOrderable()).isFalse();
    }

    @Test
    void 배송지를_추가할_수_있다() {
        // given
        Member member = generateMember();

        // when
        ShippingAddress shippingAddress1 = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        ShippingAddress shippingAddress2 = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // then
        assertThat(member.getShippingAddresses()).hasSize(2);
        assertThat(member.getShippingAddresses()).containsAll(List.of(shippingAddress1, shippingAddress2));
    }

    @Test
    void 배송지를_추가하면_수신인_주소_정보_식별자가_생성되어_저장된다() {
        // given
        Member member = generateMember();

        // when
        ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // then
        assertThat(shippingAddress.getId()).isNotNull();
        assertThat(shippingAddress.getId().value()).isNotNull();
        assertThat(shippingAddress.getReceiver()).isNotNull();
        assertThat(shippingAddress.getAddress()).isNotNull();
    }

    @Test
    void 처음_추가한_배송지는_자동으로_기본_배송지가_된다() {
        // given
        Member member = generateMember();

        // when
        ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // then
        assertThat(shippingAddress.isDefault()).isTrue();
    }

    @Test
    void 두_번째_추가한_배송지는_기본_배송지가_되지_않는다() {
        // given
        Member member = generateMemberWithShippingAddress();

        // when
        ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // then
        assertThat(shippingAddress.isDefault()).isFalse();
    }

    @Test
    void 배송지는_최대_5개까지_추가할_수_있다() {
        // given
        Member member = generateMemberWithShippingAddress(5);

        // when
        // then
        assertThatThrownBy(() -> member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void WITHDRAWN_회원은_배송지를_추가할_수_없다() {
        // given
        Member member = generateWithdrawnMember();

        // when
        // then
        assertThatThrownBy(() -> member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 기본_배송지를_변경하면_기존_기본_배송지는_해제된다() {
        // given
        Member member = generateMember();
        ShippingAddress firstAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        ShippingAddress secondAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        member.setDefaultShippingAddress(secondAddress.getId());

        // then
        assertThat(firstAddress.isDefault()).isFalse();
        assertThat(secondAddress.isDefault()).isTrue();
    }

    @Test
    void 이미_기본_배송지인_배송지를_다시_기본으로_설정해도_정상_처리된다() {
        // given
        Member member = generateMember();
        ShippingAddress firstAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        ShippingAddress secondAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        member.setDefaultShippingAddress(firstAddress.getId());

        // then
        assertThat(firstAddress.isDefault()).isTrue();
        assertThat(secondAddress.isDefault()).isFalse();
    }

    @Test
    void WITHDRAWN_회원은_기본_배송지를_설정할_수_없다() {
        // given
        Member member = generateWithdrawnMemberWithShippingAddress();
        ShippingAddress shippingAddress = member.getShippingAddresses().getFirst();

        // when
        // then
        assertThatThrownBy(() -> member.setDefaultShippingAddress(shippingAddress.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 본인_배송지가_아니면_기본_배송지로_설정할_수_없다() {
        // given
        Member member = generateMember();

        Member otherMember = generateMember();
        ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        // then
        assertThatThrownBy(() -> member.setDefaultShippingAddress(otherShippingAddress.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배송지를_수정할_수_있다() {
        // given
        Member member = generateMember();
        ShippingAddress originShippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        ModifyShippingAddressCommand modifyCommand = generateModifyShippingAddressCommand();

        // when
        ShippingAddress modified = member.modifyShippingAddress(originShippingAddress.getId(), modifyCommand);

        // then
        assertThat(modified.getId()).isEqualTo(originShippingAddress.getId());
        assertThat(modified.getReceiver()).isEqualTo(new Receiver(modifyCommand.receiverName(), modifyCommand.receiverPhoneNumber()));
        assertThat(modified.getAddress()).isEqualTo(new Address(modifyCommand.zipCode(), modifyCommand.mainAddress(), modifyCommand.detailAddress()));
        assertThat(modified.isDefault()).isTrue();
    }

    @Test
    void 배송지_수정_시_기본_배송지_여부는_변경되지_않는다() {
        // given
        Member member = generateMemberWithShippingAddress();
        ShippingAddress nonDefault = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        ShippingAddress modified = member.modifyShippingAddress(nonDefault.getId(), generateModifyShippingAddressCommand());

        assertThat(modified.isDefault()).isFalse();
    }

    @Test
    void WITHDRAWN_회원은_배송지를_수정할_수_없다() {
        // given
        Member member = generateWithdrawnMemberWithShippingAddress();
        ShippingAddress shippingAddress = member.getShippingAddresses().getFirst();

        // when
        // then
        assertThatThrownBy(() -> member.modifyShippingAddress(shippingAddress.getId(), generateModifyShippingAddressCommand()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 본인_배송지가_아니면_수정할_수_없다() {
        // given
        Member member = generateMemberWithShippingAddress();

        Member otherMember = generateMember();
        ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        // then
        assertThatThrownBy(() -> member.modifyShippingAddress(otherShippingAddress.getId(), generateModifyShippingAddressCommand()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배송지를_삭제할_수_있다() {
        // given
        Member member = generateMember();
        ShippingAddress shippingAddress1 = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        ShippingAddress shippingAddress2 = member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        member.removeShippingAddress(shippingAddress1.getId());

        // then
        assertThat(member.getShippingAddresses()).doesNotContain(shippingAddress1);

        assertThat(member.getShippingAddresses()).hasSize(1);
        assertThat(member.getShippingAddresses()).contains(shippingAddress2);
    }

    @Test
    void WITHDRAWN_회원은_배송지를_삭제할_수_없다() {
        // given
        Member member = generateWithdrawnMemberWithShippingAddress();
        ShippingAddress shippingAddress = member.getShippingAddresses().getFirst();

        // when
        // then
        assertThatThrownBy(() -> member.removeShippingAddress(shippingAddress.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 본인_배송지가_아니면_삭제할_수_없다() {
        // given
        Member member = generateMemberWithShippingAddress();

        Member otherMember = generateMember();
        ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());

        // when
        // then
        assertThatThrownBy(() -> member.removeShippingAddress(otherShippingAddress.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}