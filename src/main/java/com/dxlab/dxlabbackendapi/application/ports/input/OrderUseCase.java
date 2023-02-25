package com.dxlab.dxlabbackendapi.application.ports.input;

import com.dxlab.dxlabbackendapi.domain.model.Order;

import java.util.List;

public interface OrderUseCase {

    List<Order> getAll();

    Order createOrder(Order order);
}
