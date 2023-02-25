package com.dxlab.dxlabbackendapi.testcontainer.config;

import com.dxlab.dxlabbackendapi.testcontainer.containers.LocalStackTestContainer;
import com.dxlab.dxlabbackendapi.testcontainer.containers.PostgresTestContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
public class DbS3ContainersEnviroment {

    public static final String BUCKET_NAME = "dxlab-result-lab";

    @Container
    public static PostgreSQLContainer postgreSQLContainer = PostgresTestContainer.getInstance();

    @Container
    public static LocalStackContainer localStackContainer = LocalStackTestContainer.getInstance();

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.s3.bucket-name", () -> BUCKET_NAME);
        registry.add("cloud.aws.s3.endpoint", () -> localStackContainer.getEndpointOverride(S3));
        registry.add("cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
    }
}
