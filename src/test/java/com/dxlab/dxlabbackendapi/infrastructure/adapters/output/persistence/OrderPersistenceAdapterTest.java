package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @InjectMocks
    OrderPersistenceAdapter orderPersistenceAdapter;

    @Mock
    OrderRespository orderRespository;

    @Test
    void shouldReturnFilledOrderList() {
        List<OrderEntity> orderEntityMockList = List.of(
                OrderEntity.builder().id(1L).user("Pepito perez").build(),
                OrderEntity.builder().id(2L).user("Jose jose").build()
        );
        when(orderRespository.findAll()).thenReturn(orderEntityMockList);

        List<Order> result = orderPersistenceAdapter.getAll();

        assertThat(result)
                .hasSize(2)
                .extracting("id","user")
                .contains(tuple(1L,"Pepito perez"), tuple(2L, "Jose jose"));
    }

    @Test
    void shouldReturnOrderCreate() {
        Order order = Order.builder().user("Pepito perez").build();
        OrderEntity savedOrder = OrderEntity.builder().id(1L).user("Pepito perez").build();
        when(orderRespository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        Order saveOrder = orderPersistenceAdapter.createOrder(order);

        assertNotNull("El id de la orden no puede ser vacio", saveOrder.getId());
        assertEquals("El mapeo del usuario es incorrecto", savedOrder.getUser(), saveOrder.getUser());
    }
}