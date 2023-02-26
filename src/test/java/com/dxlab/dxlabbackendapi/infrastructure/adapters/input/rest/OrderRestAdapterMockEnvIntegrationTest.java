package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.testcontainer.config.DbContainerEnviroment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class OrderRestAdapterMockEnvIntegrationTest extends DbContainerEnviroment {

    public static final String USER_PEPITO_PEREZ = "Pepito perez";
    public static final String USER_JOSE_JOSE = "Jose jose";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql_test/order.sql")
    void getAllOrders() throws Exception {
        mockMvc.perform(get("/v1/orden/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].usuario", is(USER_PEPITO_PEREZ)))
                .andExpect(jsonPath("$[1].usuario", is(USER_JOSE_JOSE)));
    }

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(post("/v1/orden")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                              {
                                  "usuario": "%s",
                                  "examen": "Examen de colesterol",
                                  "muestraPendiente": true
                              }""", USER_PEPITO_PEREZ)))
                .andExpect(status().isCreated());
    }
}