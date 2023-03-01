package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test-s3")
class LaboratoryResultAdapterIntegrationTest extends DbS3ContainersEnviroment {

    private static final String USER_PEPITO_PEREZ = "Pepito perez";
    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/Test.pdf";

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    AmazonS3 s3;

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class AwsTestConfig {
        @Bean
        @Profile("test-s3")
        public AmazonS3 amazonS3() {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                    .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
                    .build();
        }
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localStackContainer.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    @AfterEach
    void deleteEntities() {
        orderRespository.deleteAll();
        clearS3Bucket();
    }

    private void clearS3Bucket() {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(BUCKET_NAME);
        ObjectListing objectListing = s3.listObjects(listObjectsRequest);
        while (true) {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                s3.deleteObject(BUCKET_NAME, objectSummary.getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = s3.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    @Test
    void uploadLaboratoryResult() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user(USER_PEPITO_PEREZ).examination("Gripe").build();
        orderRespository.save(orderEntity);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test2.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "1", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.idOrden").isEqualTo("1");
    }

    @Test
    void uploadLaboratoryResult_badRequestFileExist() throws IOException {
        OrderEntity orderEntity = OrderEntity.builder().user("Pepito perez").examination("Gripe").build();
        orderRespository.save(orderEntity);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "1", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar")
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
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("archivos", Files.readAllBytes(path))
                .header("Content-Disposition", "form-data; name=archivos; filename=Test1.pdf")
                .header("Content-type", "application/pdf");
        builder.part("idOrden", "2", MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=idOrden")
                .header("Content-type", "text/plain");

        webTestClient.post().uri("/v1/resultados/cargar")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Orden no encontrada con el id: 2");
    }
}
