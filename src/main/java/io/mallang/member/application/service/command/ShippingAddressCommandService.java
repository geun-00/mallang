package io.mallang.member.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.RemoveShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.*;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingAddressCommandService implements RegisterShippingAddressUseCase, UpdateDefaultShippingAddressUseCase, UpdateShippingAddressUseCase, RemoveShippingAddressUseCase {

    private final IdGenerator idGenerator;
    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    @Override
    public RegisterShippingAddressResult register(RegisterShippingAddressCommand command) {
        Member member = loadMemberPort.getById(new MemberId(command.memberIdValue()));

        ShippingAddress shippingAddress = member.addShippingAddress(
                new AddShippingAddressCommand(
                        new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                        new Address(command.zipCode(), command.mainAddress(), command.detailAddress())
                ),
                idGenerator
        );

        saveMemberPort.save(member);

        return new RegisterShippingAddressResult(shippingAddress.getId().value());
    }

    @Override
    public void update(UpdateDefaultShippingAddressCommand command) {
        Member member = loadMemberPort.getById(new MemberId(command.memberIdValue()));

        member.setDefaultShippingAddress(new ShippingAddressId(command.shippingAddressIdValue()));

        saveMemberPort.save(member);
    }

    @Override
    public void update(UpdateShippingAddressCommand command) {
        Member member = loadMemberPort.getById(new MemberId(command.memberIdValue()));

        member.modifyShippingAddress(
                new ShippingAddressId(command.shippingAddressIdValue()),
                new ModifyShippingAddressCommand(
                        new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                        new Address(command.zipCode(), command.mainAddress(), command.detailAddress())
                )
        );

        saveMemberPort.save(member);
    }

    @Override
    public void remove(RemoveShippingAddressCommand command) {
        Member member = loadMemberPort.getById(new MemberId(command.memberIdValue()));

        member.removeShippingAddress(new ShippingAddressId(command.shippingAddressIdValue()));

        saveMemberPort.save(member);
    }
}
