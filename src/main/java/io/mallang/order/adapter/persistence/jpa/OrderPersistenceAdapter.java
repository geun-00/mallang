package io.mallang.order.adapter.persistence.jpa;

import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements SaveOrderPort, LoadOrderPort {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    @Transactional
    public void save(Order order) {
        orderJpaRepository.findById(order.getId().value())
                          .ifPresentOrElse(
                                  entity -> entity.updateFrom(order),
                                  () -> orderJpaRepository.save(OrderJpaEntity.from(order))
                          );
    }

    @Override
    public Order getById(OrderId orderId) {
        return orderJpaRepository.findWithItemsById(orderId.value())
                                 .map(OrderJpaEntity::toDomain)
                                 .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
