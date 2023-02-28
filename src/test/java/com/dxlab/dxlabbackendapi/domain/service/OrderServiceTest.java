package com.dxlab.dxlabbackendapi.domain.service;

import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String USER_PEPITO_PEREZ = "Pepito perez";
    private static final String USER_JOSE_JOSE = "Jose jose";

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderOutputPort orderOutputPort;

    @Test
    void shouldReturnFilledOrderList() {
        List<Order> orderMockList = List.of(
                Order.builder().id(1L).user(USER_PEPITO_PEREZ).build(),
                Order.builder().id(2L).user(USER_JOSE_JOSE).build()
        );
        when(orderOutputPort.getAll()).thenReturn(orderMockList);

        List<Order> result = orderService.getAll();

        assertThat(result)
                .hasSize(2)
                .extracting("id","user")
                .contains(tuple(1L,USER_PEPITO_PEREZ), tuple(2L, USER_JOSE_JOSE));
    }

    @Test
    void shouldProcessOnOrderCreation_whenUserIsValid() {
        Order order = Order.builder().id(1L).user(USER_PEPITO_PEREZ).build();
        Order savedOrder = Order.builder().id(1L).user(USER_PEPITO_PEREZ).build();
        when(orderOutputPort.createOrder(order)).thenReturn(savedOrder);

        Order saveOrder = orderService.createOrder(order);

        assertThat(saveOrder).isEqualTo(savedOrder);
        verify(orderOutputPort, times(1)).createOrder(any());
    }
}