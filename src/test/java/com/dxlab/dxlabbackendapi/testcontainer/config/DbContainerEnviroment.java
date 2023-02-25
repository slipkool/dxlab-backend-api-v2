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
public class DbContainerEnviroment {

    private static final String LOCALSTACK_IMAGE_VERSION = "localstack/localstack:0.13.0";
    private static final String BUCKET_NAME = "dxlab-result-lab";

    @Container
    public static PostgreSQLContainer postgreSQLContainer = PostgresTestContainer.getInstance();
}
