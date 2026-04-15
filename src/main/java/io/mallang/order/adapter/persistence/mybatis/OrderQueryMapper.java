package io.mallang.order.adapter.persistence.mybatis;

import io.mallang.order.adapter.persistence.mybatis.model.OrderListRow;
import io.mallang.order.adapter.persistence.mybatis.model.SearchMyOrdersCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderQueryMapper {

    List<OrderListRow> selectMyOrders(SearchMyOrdersCondition condition);
}
