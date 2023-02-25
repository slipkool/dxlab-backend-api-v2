package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
public class S3Adapter implements LaboratoryResultOutputport {
    private final AmazonS3 amazonS3;
    private final S3Properties properties;

    @Override
    public void uploadFiles(LaboratoryResult laboratoryResult) {
        for (MultipartFile file: laboratoryResult.getFiles()) {
            uploadFile(laboratoryResult.getIdOrder(), file);
        }
    }

    private void uploadFile(Long idOrder, MultipartFile file) {
        try {
            String path = String.format("%s/%s", properties.getBucketName(), idOrder);
            String fileName = String.format("%s", file.getOriginalFilename()).replace(" ", "_");
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            validateObjectExist(properties.getBucketName(), String.format("%s/%s", idOrder, fileName));

            amazonS3.putObject(path, fileName, file.getInputStream(), objectMetadata);
        } catch (AmazonServiceException | IOException e) {
            throw new LaboratoryResultException("Error al cargar el archivo al repositorio", e);
        }
    }

    private void validateObjectExist(String bucketName, String fileName) {
        boolean doesItExists = amazonS3.doesObjectExist(bucketName, fileName);
        if(doesItExists)
            throw new LaboratoryResultException("El archivo ya existe para la orden solicitada");
    }
}
