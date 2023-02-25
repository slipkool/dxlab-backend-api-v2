package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.mapper;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderPersistenceMapper {

    OrderPersistenceMapper INSTANCE = Mappers.getMapper(OrderPersistenceMapper.class);

    List<Order> toOrderList(List<OrderEntity> orderEntityList);

    OrderEntity toOrderEntity(Order order);

    Order toOrder(OrderEntity orderEntity);
}
