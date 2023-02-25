package com.dxlab.dxlabbackendapi.infrastructure.adapters.config;

import com.amazonaws.services.s3.AmazonS3;
import com.dxlab.dxlabbackendapi.domain.service.LaboratoryResultService;
import com.dxlab.dxlabbackendapi.domain.service.OrderService;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.OrderPersistenceAdapter;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository.OrderRespository;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3.S3Adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderService orderService(OrderPersistenceAdapter orderPersistenceAdapter) {
        return new OrderService(orderPersistenceAdapter);
    }

    @Bean
    public OrderPersistenceAdapter orderPersistenceAdapter(OrderRespository orderRespository) {
        return new OrderPersistenceAdapter(orderRespository);
    }

    @Bean
    public LaboratoryResultService laboratoryResultService(S3Adapter s3Adapter, OrderPersistenceAdapter orderPersistenceAdapter) {
        return new LaboratoryResultService(s3Adapter, orderPersistenceAdapter);
    }

    @Bean
    public S3Adapter s3Adapter(AmazonS3 amazonS3, S3Properties properties) {
        return new S3Adapter(amazonS3, properties);
    }
}
