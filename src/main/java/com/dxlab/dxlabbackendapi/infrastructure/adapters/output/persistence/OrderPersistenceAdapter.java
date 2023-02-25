package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence;

import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.exception.OrderNotFound;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.mapper.OrderPersistenceMapper;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderOutputPort {

    private final OrderRespository orderRespository;

    @Override
    public List<Order> getAll() {
        List<OrderEntity> result = orderRespository.findAll();

        return OrderPersistenceMapper.INSTANCE.toOrderList(result);
    }

    @Override
    public Order createOrder(Order order) {
        OrderEntity orderEntity = OrderPersistenceMapper.INSTANCE.toOrderEntity(order);
        orderEntity = orderRespository.save(orderEntity);
        return OrderPersistenceMapper.INSTANCE.toOrder(orderEntity);
    }

    @Override
    public Optional<Order> getOrderById(Long idOrder) {
        Optional<OrderEntity> orderEntity = orderRespository.findById(idOrder);
        if(orderEntity.isEmpty())
            return Optional.empty();

        Order order = OrderPersistenceMapper.INSTANCE.toOrder(orderEntity.get());
        return Optional.of(order);
    }

    @Override
    @Transactional
    public boolean updateDateResultOrder(Order order) {
        OrderEntity orderEntity = OrderPersistenceMapper.INSTANCE.toOrderEntity(order);
        int result = orderRespository.updateDateResult(orderEntity.getId(), orderEntity.getReadyResult());
        if(result == 1) {
            return true;
        }
        throw new OrderNotFound("Orden no encontrada con el id: " + order.getId());
    }
}
