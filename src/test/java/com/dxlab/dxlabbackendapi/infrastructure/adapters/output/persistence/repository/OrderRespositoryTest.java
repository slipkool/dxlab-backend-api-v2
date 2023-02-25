package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository;

import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbS3ContainersEnviroment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderRespositoryTest extends DbS3ContainersEnviroment {

    @Autowired
    OrderRespository orderRespository;

    @Test
    void getAllOrders() {
        OrderEntity orderEntity1 = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        OrderEntity orderEntity2 = OrderEntity.builder().user("Jose jose").examination("Anemia").build();
        orderRespository.saveAll(List.of(
                orderEntity1, orderEntity2
        ));

        List<OrderEntity> result = orderRespository.findAll();

        assertEquals(2, result.size());
    }
}