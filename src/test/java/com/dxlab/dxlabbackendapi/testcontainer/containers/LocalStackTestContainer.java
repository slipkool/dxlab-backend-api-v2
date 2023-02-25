package com.dxlab.dxlabbackendapi.testcontainer.containers;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

public class LocalStackTestContainer extends LocalStackContainer {
    private static final String IMAGE_VERSION = "localstack/localstack:0.13.0";
    private static final String BUCKET_NAME = "dxlab-result-lab";
    public static LocalStackContainer container;


    public static LocalStackContainer getInstance() {
        if(container == null) {
            container = new LocalStackContainer(DockerImageName.parse(IMAGE_VERSION))
                    .withServices(S3);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {}
}
