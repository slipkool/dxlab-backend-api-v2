package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.mapper;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class OrderPersistenceMapperTest {

    public static final String USER_PEPITO_PEREZ = "Pepito perez";
    public static final String USER_JOSE_JOSE = "Jose jose";

    private final OrderPersistenceMapper mapper = Mappers.getMapper(OrderPersistenceMapper.class);

    @Test
    void shouldMapJpaEntityToDomain() {
        List<OrderEntity> orderEntityList = List.of(
                OrderEntity.builder().id(1L).user(USER_PEPITO_PEREZ).build(),
                OrderEntity.builder().id(2L).user(USER_JOSE_JOSE).build()
        );

        List<Order> result = mapper.toOrderList(orderEntityList);

        assertEquals("El mapeo del id de la orden es incorrecto", orderEntityList.get(0).getId(), result.get(0).getId());
        assertEquals("El mapeo del usuario es incorrecto", orderEntityList.get(0).getUser(), result.get(0).getUser());
    }

    @Test
    void shouldMapOrderEntityToOder() {
        Order order = Order.builder().id(1L).user(USER_PEPITO_PEREZ).build();

        OrderEntity result = mapper.toOrderEntity(order);

        assertEquals("El mapeo del id de la orden es incorrecto", order.getId(), result.getId());
        assertEquals("El mapeo del usuario es incorrecto", order.getUser(), result.getUser());
    }

    @Test
    void shouldMapOrderEntityToOrder() {
        OrderEntity orderEntity = OrderEntity.builder().id(1L).user(USER_PEPITO_PEREZ).build();

        Order result = mapper.toOrder(orderEntity);

        assertEquals("El mapeo del id de la orden es incorrecto", orderEntity.getId(), result.getId());
        assertEquals("El mapeo del usuario es incorrecto", orderEntity.getUser(), result.getUser());
    }
}