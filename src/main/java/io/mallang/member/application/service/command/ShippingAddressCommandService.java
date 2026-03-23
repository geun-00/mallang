package io.mallang.member.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressCommand;
import io.mallang.member.application.provided.command.model.UpdateDefaultShippingAddressCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingAddressCommandService implements RegisterShippingAddressUseCase, UpdateDefaultShippingAddressUseCase {

    private final IdGenerator idGenerator;
    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    @Override
    public ShippingAddressId register(RegisterShippingAddressCommand command) {
        Member member = loadMemberPort.getById(command.memberId());

        ShippingAddress shippingAddress = member.addShippingAddress(
                new AddShippingAddressCommand(
                        command.receiverName(),
                        command.receiverPhoneNumber(),
                        command.zipCode(),
                        command.mainAddress(),
                        command.detailAddress()
                ),
                idGenerator
        );

        saveMemberPort.save(member);

        return shippingAddress.getId();
    }

    @Override
    public void update(UpdateDefaultShippingAddressCommand command) {
        Member member = loadMemberPort.getById(command.memberId());

        member.setDefaultShippingAddress(command.shippingAddressId());

        saveMemberPort.save(member);
    }
}

