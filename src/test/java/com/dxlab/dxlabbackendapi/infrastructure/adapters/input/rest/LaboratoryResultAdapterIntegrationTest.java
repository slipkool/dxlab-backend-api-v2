package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbS3ContainersEnviroment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LaboratoryResultAdapterIntegrationTest extends DbS3ContainersEnviroment {

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void deleteEntities() {
        orderRespository.deleteAll();
    }

    @Test
    void uploadLaboratoryResult() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        orderRespository.save(orderEntity);


        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf"))).header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf");
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf"))).header("Content-Disposition", "form-data; name=archivos; filename=Test2.pdf");
        builder.part("idOrden", "2", MediaType.TEXT_PLAIN).header("Content-Disposition", "form-data; name=idOrden").header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar-resultados")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated();
    }
}
