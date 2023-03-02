package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;
import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.exception.NotFoundException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper.LaboratoryResultRestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3AdapterTest {
    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/";

    @Mock
    AmazonS3 amazonS3;
    @Mock
    S3Properties properties;

    @InjectMocks
    S3Adapter s3Adapter;

    private final LaboratoryResultRestMapper mapper = Mappers.getMapper(LaboratoryResultRestMapper.class);

    @Test
    void shouldUploadFiles_whenNoExceptions() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();
        LaboratoryResult laboratoryResult = mapper.toLaboratoryResult(resultadoLaboratorioRequest);
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(false);
        when(amazonS3.putObject(anyString(), anyString(), any(), any())).thenReturn(null);

        String result = assertDoesNotThrow(() -> {
            s3Adapter.uploadFiles(laboratoryResult);
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldException_whenFileExist() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();
        LaboratoryResult laboratoryResult = mapper.toLaboratoryResult(resultadoLaboratorioRequest);
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(true);

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> s3Adapter.uploadFiles(laboratoryResult));

        assertEquals("El archivo ya existe para la orden solicitada", exception.getMessage());
    }

    @Test
    void shouldException_whenErrorFileUpload() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();
        LaboratoryResult laboratoryResult = mapper.toLaboratoryResult(resultadoLaboratorioRequest);
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(false);
        when(amazonS3.putObject(anyString(), anyString(), any(), any())).thenThrow(AmazonServiceException.class);

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> s3Adapter.uploadFiles(laboratoryResult));

        assertEquals("Error al cargar el archivo al repositorio", exception.getMessage());
    }

    @Test
    void shouldException_whenGetLabResultFileListFileNotFoundObjectListing() {
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.listObjects(anyString(), anyString())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> s3Adapter.getLabResultFileList(1L));

        assertEquals("No hay archivos para la orden: 1", exception.getMessage());
    }

    @Test
    void shouldException_whenGetLabResultFileListFileNotFoundS3ObjectSummary() {
        ObjectListing objectListingMock = new ObjectListing();
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.listObjects(anyString(), anyString())).thenReturn(objectListingMock);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> s3Adapter.getLabResultFileList(1L));

        assertEquals("No hay archivos para la orden: 1", exception.getMessage());
    }

    @Test
    void shouldGetLabResultFileListFile_whenNoException() {
        ObjectListing objectListingMock = new ObjectListing();
        S3ObjectSummary file = new S3ObjectSummary();
        file.setKey("Test.pdf");
        objectListingMock.getObjectSummaries().add(file);
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.listObjects(anyString(), anyString())).thenReturn(objectListingMock);

        LaboratoryResultInfo result = s3Adapter.getLabResultFileList(1L);

        assertEquals(1L, result.getOrderId());
        assertFalse(result.getNameFileList().isEmpty());
    }

    @Test
    void shouldException_whenDeleteLabResultFile() {
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(false);

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> s3Adapter.deleteLabResultFile(1L, "Test.pdf"));

        assertEquals("El archivo no existe para la orden solicitada", exception.getMessage());
    }

    @Test
    void shouldDeleteLabResultFile_whenNoException() {
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(true);
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());

        String result = assertDoesNotThrow(() -> {
            s3Adapter.deleteLabResultFile(1L, "Test.pdf");
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldException_whenDeleteLabResultFolder() {
        ObjectListing objectListingMock = new ObjectListing();
        S3ObjectSummary file = new S3ObjectSummary();
        file.setKey("Test.pdf");
        objectListingMock.getObjectSummaries().add(file);
        when(properties.getBucketName()).thenReturn("BUCKET");

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> s3Adapter.deleteLabResultFile(1L, "Test.pdf"));

        assertEquals("El archivo no existe para la orden solicitada", exception.getMessage());
    }

    @Test
    void shouldDeleteLabResultFolder_whenNoException() {
        ObjectListing objectListingMock = new ObjectListing();
        S3ObjectSummary file = new S3ObjectSummary();
        file.setKey("Test.pdf");
        objectListingMock.setTruncated(false);
        objectListingMock.getObjectSummaries().add(file);
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.doesBucketExistV2(anyString())).thenReturn(true);
        when(amazonS3.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListingMock);
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());

        String result = assertDoesNotThrow(() -> {
            s3Adapter.deleteLabResultFolder(1L);
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldDownloadLabResultFile() throws UnsupportedEncodingException {
        byte[] byteMock = "Test".getBytes();
        S3Object s3ObjectMock = new S3Object();
        s3ObjectMock.setObjectContent(new StringInputStream("Test"));
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3ObjectMock);

        byte[] result = s3Adapter.downloadLabResultFile(1L, "Test.pdf");

        assertArrayEquals(byteMock, result);
    }

    @Test
    void shouldException_whenDownloadLabResultFile() {
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.getObject(anyString(), anyString())).thenThrow(AmazonServiceException.class);

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> s3Adapter.downloadLabResultFile(1L, "Test.pdf"));

        assertEquals("Error al descargar el archivo Test.pdf del repositorio", exception.getMessage());
    }

    @Test
    void shouldDownloadZipLabResultFile() throws UnsupportedEncodingException {
        ObjectListing objectListingMock = new ObjectListing();
        S3ObjectSummary file = new S3ObjectSummary();
        file.setKey("Test.pdf");
        objectListingMock.getObjectSummaries().add(file);
        S3Object s3ObjectMock = new S3Object();
        s3ObjectMock.setObjectContent(new StringInputStream("Test"));
        when(properties.getBucketName()).thenReturn("BUCKET");
        when(amazonS3.listObjects(anyString(), anyString())).thenReturn(objectListingMock);
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3ObjectMock);

        byte[] result = s3Adapter.downloadZipLabResultFile(1L);

        assertTrue(result.length > 0);
    }
}