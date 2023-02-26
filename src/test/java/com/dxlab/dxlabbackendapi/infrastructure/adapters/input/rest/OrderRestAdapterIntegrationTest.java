package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbContainerEnviroment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class OrderRestAdapterIntegrationTest extends DbContainerEnviroment {

    public static final String USER_PEPITO_PEREZ = "Pepito perez";
    public static final String USER_JOSE_JOSE = "Jose jose";

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void deleteEntities() {
        orderRespository.deleteAll();
    }

    @Test
    void getAllOrders() {
        OrderEntity orderEntity1 = OrderEntity.builder().user(USER_PEPITO_PEREZ).examination("Gripe").build();
        OrderEntity orderEntity2 = OrderEntity.builder().user(USER_JOSE_JOSE).examination("Anemia").build();

        orderRespository.saveAll(List.of(
                orderEntity1, orderEntity2
        ));

        webTestClient.get().uri("/v1/orden/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].usuario").isEqualTo(USER_PEPITO_PEREZ)
                .jsonPath("$[0].examen").isEqualTo("Gripe")
                .jsonPath("$[1].id").isEqualTo("2")
                .jsonPath("$[1].usuario").isEqualTo(USER_JOSE_JOSE)
                .jsonPath("$[1].examen").isEqualTo("Anemia")
                .jsonPath("$", hasSize(2));
    }

    @Test
    void createOrder() {
        webTestClient.post().uri("/v1/orden")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format("""
                              {
                                  "usuario": "%s",
                                  "examen": "Examen de colesterol",
                                  "muestraPendiente": true
                              }""", USER_PEPITO_PEREZ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.usuario").isEqualTo(USER_PEPITO_PEREZ)
                .jsonPath("$.examen").isEqualTo("Examen de colesterol");
    }
}