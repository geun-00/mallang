package io.mallang.product.adapter.persistence.mybatis;

import io.mallang.product.adapter.persistence.mybatis.model.ProductListRow;
import io.mallang.product.adapter.persistence.mybatis.model.SearchProductCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductQueryMapper {

    List<ProductListRow> selectProducts(SearchProductCondition condition);
}
