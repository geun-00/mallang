package io.mallang.order.adapter.persistence.mybatis;

import io.mallang.order.adapter.persistence.mybatis.model.OrderListRow;
import io.mallang.order.adapter.persistence.mybatis.model.OrderDetailRow;
import io.mallang.order.adapter.persistence.mybatis.model.SearchMyOrdersCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderQueryMapper {

    List<OrderListRow> selectMyOrders(SearchMyOrdersCondition condition);

    List<OrderDetailRow> selectOrderDetailRows(@Param("orderIdValue") String orderIdValue);
}
