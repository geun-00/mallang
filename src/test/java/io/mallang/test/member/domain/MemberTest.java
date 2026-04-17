package io.mallang.test.member.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.fixtures.CommonFixture;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberPasswordEncoder;
import io.mallang.member.domain.MemberStatus;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import io.mallang.member.domain.command.RestoreMemberCommand;
import io.mallang.member.domain.exception.InvalidMemberStateException;
import io.mallang.member.domain.exception.ShippingAddressLimitException;
import io.mallang.member.domain.exception.ShippingAddressNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.mallang.assertions.MemberAssertions.isDerivedFrom;
import static io.mallang.assertions.MemberAssertions.isRestoredFrom;
import static io.mallang.fixtures.MemberFixture.*;
import static io.mallang.member.domain.Member.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Member 엔티티")
class MemberTest {

    @Nested
    class 복원 {

        @Test
        void MemberRestoreCommand의_정보로_복원한다() {
            // given
            Member original = generateMemberWithShippingAddress(3);
            original.withdraw(CommonFixture.generateClockHolder());

            RestoreMemberCommand restoreCommand = new RestoreMemberCommand(
                    original.getId(),
                    original.getEmail(),
                    original.getNickname(),
                    original.getPassword(),
                    original.getJoinedAt(),
                    original.getStatus(),
                    original.getWithdrawnAt(),
                    original.getShippingAddresses(),
                    true
            );

            // when
            Member restored = Member.restore(restoreCommand);

            // then
            assertThat(restored).satisfies(isRestoredFrom(restoreCommand));
        }
    }

    @Nested
    class 생성 {

        @Test
        void 유효한_정보로_회원을_생성하면_ACTIVE_상태가_된다() {
            // given
            CreateMemberCommand createCommand = generateCreateCommand();

            // when
            Member member = create(createCommand, generatePasswordEncoder(), CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());

            // then
            assertThat(member.isActive()).isTrue();
        }

        @Test
        void 회원을_생성하면_커맨드의_정보가_저장된다() {
            // given
            CreateMemberCommand command = generateCreateCommand();
            MemberPasswordEncoder passwordEncoder = generatePasswordEncoder();

            // when
            Member member = create(command, passwordEncoder, CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());

            // then
            assertThat(member).satisfies(isDerivedFrom(command, passwordEncoder));
        }

        @Test
        void 회원을_생성하면_가입_시간이_기록된다() {
            // given
            ClockHolder clockHolder = CommonFixture.generateClockHolder();

            // when
            Member member = create(generateCreateCommand(), generatePasswordEncoder(), CommonFixture.generateIdGenerator(), clockHolder);

            // then
            assertThat(member.getJoinedAt()).isEqualTo(clockHolder.now());
        }

        @ParameterizedTest
        @MethodSource("io.mallang.TestDataSource#invalidPasswordLengthValues")
        void 비밀번호는_8자_이상_20자_이하여야_한다(String invalidPassword) {
            assertThatThrownBy(() -> generateMember(invalidPassword))
                    .isInstanceOf(InvalidValueException.class);
        }

        @ParameterizedTest
        @MethodSource("io.mallang.TestDataSource#invalidPasswordCompositionValues")
        void 비밀번호는_영문_숫자_특수문자를_포함해야_한다(String invalidPassword) {
            assertThatThrownBy(() -> generateMember(invalidPassword))
                    .isInstanceOf(InvalidValueException.class);
        }

        @ParameterizedTest
        @MethodSource("io.mallang.TestDataSource#invalidPasswordCharacterValues")
        void 비밀번호는_허용된_문자로만_구성되어야_한다(String invalidPassword) {
            assertThatThrownBy(() -> generateMember(invalidPassword))
                    .isInstanceOf(InvalidValueException.class);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"   "})
        void 식별자에_null이나_공백이_할당될_수_없다(String invalidId) {
            assertThatThrownBy(() -> create(generateCreateCommand(), generatePasswordEncoder(), () -> invalidId, CommonFixture.generateClockHolder()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 회원이_생성되면_식별자가_할당된다() {
            // given
            Member member = Member.create(generateCreateCommand(), generatePasswordEncoder(), CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());

            // then
            assertThat(member.getId()).isNotNull();
            assertThat(member.getId().value()).isNotNull();
        }

        @Test
        void 회원을_생성하면_비밀번호가_해싱되어_저장된다() {
            // given
            CreateMemberCommand createCommand = generateCreateCommand();
            String rawPassword = createCommand.password();

            // when
            Member member = Member.create(createCommand, generatePasswordEncoder(), CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());

            // then
            assertThat(member.getPassword().value()).isNotEqualTo(rawPassword);
        }
    }

    @Nested
    class 상태 {

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
            ClockHolder clockHolder = CommonFixture.generateClockHolder();
            assertThat(member.getWithdrawnAt()).isNull();

            // when
            member.withdraw(clockHolder);

            // then
            assertThat(member.getWithdrawnAt()).isEqualTo(clockHolder.now());
        }

        @Test
        void 이미_탈퇴한_회원은_다시_탈퇴할_수_없다() {
            // given
            Member member = generateWithdrawnMember();

            // when & then
            assertThatThrownBy(() -> member.withdraw(CommonFixture.generateClockHolder()))
                    .isInstanceOf(InvalidMemberStateException.class);
        }

        @Test
        void ACTIVE_회원은_주문_할_수_있다() {
            // given
            Member member = generateMember();

            // when & then
            assertThat(member.isOrderable()).isTrue();
        }

        @Test
        void 탈퇴한_회원은_주문할_수_없다() {
            // given
            Member member = generateWithdrawnMember();

            // when & then
            assertThat(member.isOrderable()).isFalse();
        }
    }

    @Nested
    class 배송지_관리 {

        @Test
        void 배송지를_추가할_수_있다() {
            // given
            Member member = generateMember();

            // when
            ShippingAddress shippingAddress1 = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
            ShippingAddress shippingAddress2 = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // then
            assertThat(member.getShippingAddresses()).hasSize(2);
            assertThat(member.getShippingAddresses()).containsAll(List.of(shippingAddress1, shippingAddress2));
        }

        @Test
        void 배송지를_추가하면_수신인_주소_정보_식별자가_생성되어_저장된다() {
            // given
            Member member = generateMember();

            // when
            ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

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
            ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // then
            assertThat(shippingAddress.isDefault()).isTrue();
        }

        @Test
        void 두_번째_추가한_배송지는_기본_배송지가_되지_않는다() {
            // given
            Member member = generateMemberWithShippingAddress();

            // when
            ShippingAddress shippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // then
            assertThat(shippingAddress.isDefault()).isFalse();
        }

        @Test
        void 배송지는_최대_5개까지_추가할_수_있다() {
            // given
            Member member = generateMemberWithShippingAddress(5);

            // when & then
            assertThatThrownBy(() -> member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator()))
                    .isInstanceOf(ShippingAddressLimitException.class);
        }

        @Test
        void WITHDRAWN_회원은_배송지를_추가할_수_없다() {
            // given
            Member member = generateWithdrawnMember();

            // when & then
            assertThatThrownBy(() -> member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator()))
                    .isInstanceOf(InvalidMemberStateException.class);
        }

        @Test
        void 기본_배송지를_변경하면_기존_기본_배송지는_해제된다() {
            // given
            Member member = generateMember();
            ShippingAddress firstAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
            ShippingAddress secondAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

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
            ShippingAddress firstAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
            ShippingAddress secondAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

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

            // when & then
            assertThatThrownBy(() -> member.setDefaultShippingAddress(shippingAddress.getId()))
                    .isInstanceOf(InvalidMemberStateException.class);
        }

        @Test
        void 본인_배송지가_아니면_기본_배송지로_설정할_수_없다() {
            // given
            Member member = generateMember();
            Member otherMember = generateMember();
            ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // when & then
            assertThatThrownBy(() -> member.setDefaultShippingAddress(otherShippingAddress.getId()))
                    .isInstanceOf(ShippingAddressNotFoundException.class);
        }

        @Test
        void 배송지를_수정할_수_있다() {
            // given
            Member member = generateMember();
            ShippingAddress originShippingAddress = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
            ModifyShippingAddressCommand modifyCommand = generateModifyShippingAddressCommand();

            // when
            ShippingAddress modified = member.modifyShippingAddress(originShippingAddress.getId(), modifyCommand);

            // then
            assertThat(modified.getId()).isEqualTo(originShippingAddress.getId());
            assertThat(modified.getReceiver()).isEqualTo(modifyCommand.receiver());
            assertThat(modified.getAddress()).isEqualTo(modifyCommand.address());
            assertThat(modified.isDefault()).isTrue();
        }

        @Test
        void 배송지_수정_시_기본_배송지_여부는_변경되지_않는다() {
            // given
            Member member = generateMemberWithShippingAddress();
            ShippingAddress nonDefault = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // when
            ShippingAddress modified = member.modifyShippingAddress(nonDefault.getId(), generateModifyShippingAddressCommand());

            // then
            assertThat(modified.isDefault()).isFalse();
        }

        @Test
        void WITHDRAWN_회원은_배송지를_수정할_수_없다() {
            // given
            Member member = generateWithdrawnMemberWithShippingAddress();
            ShippingAddress shippingAddress = member.getShippingAddresses().getFirst();

            // when & then
            assertThatThrownBy(() -> member.modifyShippingAddress(shippingAddress.getId(), generateModifyShippingAddressCommand()))
                    .isInstanceOf(InvalidMemberStateException.class);
        }

        @Test
        void 본인_배송지가_아니면_수정할_수_없다() {
            // given
            Member member = generateMemberWithShippingAddress();
            Member otherMember = generateMember();
            ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // when & then
            assertThatThrownBy(() -> member.modifyShippingAddress(otherShippingAddress.getId(), generateModifyShippingAddressCommand()))
                    .isInstanceOf(ShippingAddressNotFoundException.class);
        }

        @Test
        void 배송지를_삭제할_수_있다() {
            // given
            Member member = generateMember();
            ShippingAddress shippingAddress1 = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
            ShippingAddress shippingAddress2 = member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

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

            // when & then
            assertThatThrownBy(() -> member.removeShippingAddress(shippingAddress.getId()))
                    .isInstanceOf(InvalidMemberStateException.class);
        }

        @Test
        void 본인_배송지가_아니면_삭제할_수_없다() {
            // given
            Member member = generateMemberWithShippingAddress();
            Member otherMember = generateMember();
            ShippingAddress otherShippingAddress = otherMember.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());

            // when & then
            assertThatThrownBy(() -> member.removeShippingAddress(otherShippingAddress.getId()))
                    .isInstanceOf(ShippingAddressNotFoundException.class);
        }
    }
}
