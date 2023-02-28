package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryFile;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class S3Adapter implements LaboratoryResultOutputport {
    private final AmazonS3 amazonS3;
    private final S3Properties properties;

    @Override
    public void uploadFiles(LaboratoryResult laboratoryResult) {
        for (LaboratoryFile laboratoryFile: laboratoryResult.getFiles()) {
            uploadFile(laboratoryResult.getIdOrder(), laboratoryFile);
        }
    }

    private void uploadFile(Long idOrder, LaboratoryFile laboratoryFile) {
        try {
            String path = String.format("%s/%s", properties.getBucketName(), idOrder);
            String fileName = String.format("%s", laboratoryFile.getFileName());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(laboratoryFile.getContentType());
            objectMetadata.setContentLength(laboratoryFile.getSize());

            validateObjectExist(properties.getBucketName(), String.format("%s/%s", idOrder, fileName));

            amazonS3.putObject(path, fileName, laboratoryFile.getInputStreamFile(), objectMetadata);
        } catch (AmazonServiceException e) {
            throw new LaboratoryResultException("Error al cargar el archivo al repositorio", e);
        }
    }

    private void validateObjectExist(String bucketName, String fileName) {
        boolean doesItExists = amazonS3.doesObjectExist(bucketName, fileName);
        if(doesItExists)
            throw new LaboratoryResultException("El archivo ya existe para la orden solicitada");
    }
}
