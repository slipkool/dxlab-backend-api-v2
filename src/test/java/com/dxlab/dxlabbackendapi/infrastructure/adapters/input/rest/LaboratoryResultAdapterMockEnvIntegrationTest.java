package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dxlab.dxlabbackendapi.testcontainer.config.DbS3ContainersEnviroment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test-s3")
class LaboratoryResultAdapterMockEnvIntegrationTest extends DbS3ContainersEnviroment {

    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/Test.pdf";

    @Autowired
    AmazonS3 s3;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localStackContainer.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

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

    @AfterEach
    void deleteEntities() {
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
    @Sql("/sql_test/order.sql")
    void uploadLaboratoryResult() throws Exception {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        MockPart  idOrder = new MockPart("idOrden", "1".getBytes());
        MockMultipartFile file1 = new MockMultipartFile("archivos", "Test1.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MockMultipartFile file2 = new MockMultipartFile("archivos", "Test2.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));

        mockMvc.perform(multipart("/v1/resultados/cargar")
                        .file(file1)
                        .file(file2)
                        .part(idOrder)
                        .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrden", is(1)));
    }

    @Test
    @Sql("/sql_test/order.sql")
    void uploadLaboratoryResult_badRequestFileExist() throws Exception {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        MockPart  idOrder = new MockPart("idOrden", "1".getBytes());
        MockMultipartFile file1 = new MockMultipartFile("archivos", "Test1.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MockMultipartFile file2 = new MockMultipartFile("archivos", "Test1.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));

        mockMvc.perform(multipart("/v1/resultados/cargar")
                        .file(file1)
                        .file(file2)
                        .part(idOrder)
                        .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("El archivo ya existe para la orden solicitada")));
    }

    @Test
    void uploadLaboratoryResult_badRequestOrderNotFound() throws Exception {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF);
        MockPart  idOrder = new MockPart("idOrden", "1".getBytes());
        MockMultipartFile file1 = new MockMultipartFile("archivos", "Test1.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MockMultipartFile file2 = new MockMultipartFile("archivos", "Test1.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));

        mockMvc.perform(multipart("/v1/resultados/cargar")
                        .file(file1)
                        .file(file2)
                        .part(idOrder)
                        .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Orden no encontrada con el id: 1")));
    }
}
