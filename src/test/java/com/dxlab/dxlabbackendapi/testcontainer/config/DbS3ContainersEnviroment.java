package com.dxlab.dxlabbackendapi.testcontainer.config;

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

    private static final String LOCALSTACK_IMAGE_VERSION = "localstack/localstack:0.13.0";
    private static final String BUCKET_NAME = "dxlab-result-lab";

    @Container
    public static PostgreSQLContainer postgreSQLContainer = PostgresTestContainer.getInstance();

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse(LOCALSTACK_IMAGE_VERSION))
                    .withServices(S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("event-processing.order-event-bucket", () -> BUCKET_NAME);
        registry.add("cloud.aws.s3.endpoint", () -> localStack.getEndpointOverride(S3));
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
    }
}
