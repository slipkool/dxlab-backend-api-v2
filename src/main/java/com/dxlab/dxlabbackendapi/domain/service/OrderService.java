package com.dxlab.dxlabbackendapi.domain.service;

import com.dxlab.dxlabbackendapi.application.ports.input.OrderUseCase;
import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderOutputPort orderOutputPort;

    @Override
    public List<Order> getAll() {
        return orderOutputPort.getAll();
    }

    @Override
    public Order createOrder(Order order) {
        return orderOutputPort.createOrder(order);
    }
}
