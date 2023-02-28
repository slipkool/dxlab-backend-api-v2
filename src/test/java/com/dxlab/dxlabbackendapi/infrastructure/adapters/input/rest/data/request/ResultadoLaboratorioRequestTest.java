package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ResultadoLaboratorioRequestTest {
    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/";
    public static final int MAX_LENGTH_FILES = 3;
    public static final int MAX_FILE_SIZE_MB = 3;
    public static final String PARAM_ID_FILES = "archivos";

    private Validator validator;

    @BeforeEach
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    void shouldException_whenRequiredFieldsAreEmpty() {
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = new ResultadoLaboratorioRequest();

        final var violations = validator.validate(resultadoLaboratorioRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(e -> e.getMessage().equals("El id de la orden no debe ser vacio")));
        assertTrue(violations.stream().anyMatch(e -> e.getMessage().equals("Debe cargar al menos un archivo")));
    }

    @Test
    void shouldException_whenMaxLengthFiles() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile(PARAM_ID_FILES, "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file, file, file, file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> resultadoLaboratorioRequest.validateSelf(MAX_LENGTH_FILES, MAX_FILE_SIZE_MB));

        assertEquals(String.format("No se pueden cargar más de %s archivos", MAX_LENGTH_FILES), exception.getMessage());
    }

    @Test
    void shouldException_whenMaxFileSizeMb() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test_3MB.pdf");
        MockMultipartFile file = new MockMultipartFile(PARAM_ID_FILES, "Test_3MB.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> resultadoLaboratorioRequest.validateSelf(MAX_LENGTH_FILES, MAX_FILE_SIZE_MB));

        assertEquals(String.format("Los archivos deben ser menores a %sMB", MAX_FILE_SIZE_MB), exception.getMessage());
    }

    @Test
    void shouldException_whenNotSupportedExtension() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test_noValidExtension.doc");
        MockMultipartFile file = new MockMultipartFile(PARAM_ID_FILES, "Test_noValidExtension.doc", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> resultadoLaboratorioRequest.validateSelf(MAX_LENGTH_FILES, MAX_FILE_SIZE_MB));

        assertEquals("La extensión del documento no es válido(png, jpg, jpeg, pdf)", exception.getMessage());
    }

    @Test
    void shouldException_whenNoSupportedContentType() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile(PARAM_ID_FILES, "Test.pdf", MediaType.APPLICATION_XML_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        LaboratoryResultException exception = assertThrows(LaboratoryResultException.class, () -> resultadoLaboratorioRequest.validateSelf(MAX_LENGTH_FILES, MAX_FILE_SIZE_MB));

        assertEquals("El tipo de contenido del documento no es válido(png, jpg, jpeg, pdf)", exception.getMessage());
    }

    @Test
    void shouldProcess_whenNoException() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile(PARAM_ID_FILES, "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        String result = assertDoesNotThrow(() -> {
            resultadoLaboratorioRequest.validateSelf(MAX_LENGTH_FILES, MAX_FILE_SIZE_MB);
            return "OK";
        });

        assertEquals("OK", result);
    }
}