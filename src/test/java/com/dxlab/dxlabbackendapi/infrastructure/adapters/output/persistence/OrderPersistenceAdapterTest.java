package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    private static final String USER_PEPITO_PEREZ = "Pepito perez";
    private static final String USER_JOSE_JOSE = "Jose jose";

    @InjectMocks
    OrderPersistenceAdapter orderPersistenceAdapter;

    @Mock
    OrderRespository orderRespository;

    @Test
    void shouldReturnFilledOrderList() {
        Page<OrderEntity> page = mock(Page.class);
        List<OrderEntity> orderEntityMockList = List.of(
                OrderEntity.builder().id(1L).user(USER_PEPITO_PEREZ).build(),
                OrderEntity.builder().id(2L).user(USER_JOSE_JOSE).build()
        );
        when(page.getContent()).thenReturn(orderEntityMockList);
        when(orderRespository.findAll(any(Pageable.class))).thenReturn(page);

        List<Order> result = orderPersistenceAdapter.getAll();

        assertThat(result)
                .hasSize(2)
                .extracting("id","user")
                .contains(tuple(1L, USER_PEPITO_PEREZ), tuple(2L, USER_JOSE_JOSE));
    }

    @Test
    void shouldReturnOrderCreate() {
        Order order = Order.builder().user(USER_PEPITO_PEREZ).build();
        OrderEntity savedOrder = OrderEntity.builder().id(1L).user(USER_PEPITO_PEREZ).build();
        when(orderRespository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        Order saveOrder = orderPersistenceAdapter.createOrder(order);

        assertNotNull("El id de la orden no puede ser vacio", saveOrder.getId());
        assertEquals("El mapeo del usuario es incorrecto", savedOrder.getUser(), saveOrder.getUser());
    }
}