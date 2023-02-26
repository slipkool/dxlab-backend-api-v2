package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbS3ContainersEnviroment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LaboratoryResultAdapterIntegrationTest extends DbS3ContainersEnviroment {

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    AmazonS3 s3;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localStackContainer.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    @AfterEach
    void deleteEntities() {
        orderRespository.deleteAll();
    }

    @TestConfiguration
    static class AwsTestConfig {
        @Bean
        @Profile("test")
        public AmazonS3 amazonS3() {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                    .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
                    .build();
        }
    }

    @Test
    void uploadLaboratoryResult() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        orderRespository.save(orderEntity);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test2.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "1", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar-resultados")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void uploadLaboratoryResult_badRequestFileExist() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        orderRespository.save(orderEntity);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "1", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar-resultados")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("El archivo ya existe para la orden solicitada");
    }

    @Test
    void uploadLaboratoryResult_badRequestOrderNotFound() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        orderRespository.save(orderEntity);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(Paths.get("src/main/resources/static/Test.pdf")))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "2", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar-resultados")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Orden no encontrada con el id: 2");
    }
}
