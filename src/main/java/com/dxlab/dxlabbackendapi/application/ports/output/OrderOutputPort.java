package com.dxlab.dxlabbackendapi.application.ports.output;

import com.dxlab.dxlabbackendapi.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderOutputPort {
    List<Order> getAll();

    Order createOrder(Order order);

    Optional<Order> getOrderById(Long idOrder);

    boolean updateDateResultOrder(Order order);
}
