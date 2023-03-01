package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryFile;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
public class S3Adapter implements LaboratoryResultOutputport {
    public static final String STRING_TWO_PARAMS = "%s/%s";
    private final AmazonS3 amazonS3;
    private final S3Properties properties;

    @Override
    public void uploadFiles(LaboratoryResult laboratoryResult) {
        for (LaboratoryFile laboratoryFile: laboratoryResult.getFiles()) {
            uploadFile(laboratoryResult.getOrderId(), laboratoryFile);
        }
    }

    @Override
    public LaboratoryResultInfo getLabResultFileList(Long orderId) {
        ObjectListing objectListing = amazonS3.listObjects(properties.getBucketName(), String.valueOf(orderId));
        if(objectListing == null) {
            throw new LaboratoryResultException("No hay archivos para la orden: " + orderId);
        }

        List<S3ObjectSummary> s3ObjectSummariesList = objectListing.getObjectSummaries();
        if(s3ObjectSummariesList.isEmpty()) {
            throw new LaboratoryResultException("No hay archivos para la orden: " + orderId);
        }

        List<String> nameFileList =  s3ObjectSummariesList.stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> key.substring(key.indexOf("/") + 1))
                .collect(Collectors.toList());

        return LaboratoryResultInfo.builder()
                .orderId(orderId)
                .nameFileList(nameFileList)
                .build();
    }

    @Override
    public void deleteLabResultFile(Long orderId, String fileName) {
        String path = String.format(STRING_TWO_PARAMS, orderId, fileName);
        boolean doesItExists = amazonS3.doesObjectExist(properties.getBucketName(), path);
        if(!doesItExists)
            throw new LaboratoryResultException("El archivo no existe para la orden solicitada");

        try {
            amazonS3.deleteObject(properties.getBucketName(), path);
        } catch (AmazonServiceException e) {
            throw new LaboratoryResultException(String.format("Error al eliminar el archivo  %s del repositorio", fileName), e);
        }
    }

    @Override
    public void deleteLabResultFolder(Long orderId) {
        if(amazonS3.doesBucketExistV2(properties.getBucketName())) {
            try {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                        .withBucketName(properties.getBucketName())
                        .withPrefix(String.valueOf(orderId));
                ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
                while (true) {
                    for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                        amazonS3.deleteObject(properties.getBucketName(), objectSummary.getKey());
                    }
                    if (objectListing.isTruncated()) {
                        objectListing = amazonS3.listNextBatchOfObjects(objectListing);
                    } else {
                        break;
                    }
                }
            } catch (AmazonServiceException e) {
                throw new LaboratoryResultException(String.format("Error al eliminar los archivos de la orden %s", orderId), e);
            }
        }
    }

    @Override
    public byte[] downloadLabResultFile(Long orderId, String fileName) {
        String path = String.format(STRING_TWO_PARAMS, orderId, fileName);
        final S3Object s3Object = amazonS3.getObject(properties.getBucketName(), path);
        try {
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch(final IOException e) {
            throw new LaboratoryResultException(String.format("Error al descargar el archivo  %s del repositorio", fileName), e);
        }
    }

    @Override
    public byte[] downloadZipLabResultFile(Long orderId) {
        LaboratoryResultInfo laboratoryResultInfo = getLabResultFileList(orderId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
        try {
            for (String fileName : laboratoryResultInfo.getNameFileList()) {
                ZipEntry zip = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zip);
                InputStream is = new ByteArrayInputStream(downloadLabResultFile(orderId, fileName));
                IOUtils.copy(is, zipOutputStream);
                is.close();
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            return baos.toByteArray();
        } catch(final IOException e) {
            throw new LaboratoryResultException(String.format("Error al descargar el archivo zip de la orden %s", orderId), e);
        }
    }

    private void uploadFile(Long orderId, LaboratoryFile laboratoryFile) {
        try {
            String path = String.format(STRING_TWO_PARAMS, properties.getBucketName(), orderId);
            String fileName = String.format("%s", laboratoryFile.getFileName());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(laboratoryFile.getContentType());
            objectMetadata.setContentLength(laboratoryFile.getSize());

            validateObjectExist(properties.getBucketName(), String.format(STRING_TWO_PARAMS, orderId, fileName));

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
