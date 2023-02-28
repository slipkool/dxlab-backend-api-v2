package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
}