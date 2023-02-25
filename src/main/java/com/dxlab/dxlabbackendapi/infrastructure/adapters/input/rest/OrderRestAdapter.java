package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.application.ports.input.OrderUseCase;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.OrdenRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.OrdenResponse;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper.OrderRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/orden")
@RequiredArgsConstructor
public class OrderRestAdapter {

    private final OrderUseCase orderUseCase;

    @GetMapping("/todos")
    public ResponseEntity<List<OrdenResponse>> getAllOrders() {
        List<Order> result = orderUseCase.getAll();

        return ResponseEntity.ok().body(OrderRestMapper.INSTANCE.toOrdenResponseList(result));
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> createOrder(@RequestBody @Valid OrdenRequest ordenRequest) {
        Order order = OrderRestMapper.INSTANCE.toOrder(ordenRequest);

        order = orderUseCase.createOrder(order);

        URI location = URI.create(String.format("/orden/%s", order.getId()));
        return ResponseEntity.created(location).body(OrderRestMapper.INSTANCE.toOrdenResponse(order));
    }
}
