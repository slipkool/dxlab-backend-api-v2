package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbContainerEnviroment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderRestAdapterIntegrationTest extends DbContainerEnviroment {

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void deleteEntities() {
        orderRespository.deleteAll();
    }

    @Test
    void getAllOrders() {
        OrderEntity orderEntity1 = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        OrderEntity orderEntity2 = OrderEntity.builder().user("Jose jose").examination("Anemia").build();

        orderRespository.saveAll(List.of(
                orderEntity1, orderEntity2
        ));

        webTestClient.get().uri("/v1/orden/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.*", hasSize(2));
    }

    @Test
    void createOrder() {
        webTestClient.post().uri("/v1/orden")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                              {
                                  "usuario": "pepito perez",
                                  "examen": "Examen de colesterol",
                                  "muestraPendiente": true
                              }""")
                .exchange()
                .expectStatus().isCreated();
    }
}