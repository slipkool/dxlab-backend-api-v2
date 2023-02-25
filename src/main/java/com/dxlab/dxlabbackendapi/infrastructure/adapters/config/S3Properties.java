package com.dxlab.dxlabbackendapi.infrastructure.adapters.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class S3Properties {
    private String bucketName;
    private Integer maxLengthFiles;
    private Integer maxFileSizeMb;
}
