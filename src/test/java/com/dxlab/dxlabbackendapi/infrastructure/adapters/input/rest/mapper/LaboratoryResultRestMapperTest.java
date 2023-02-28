package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.util.AssertionErrors.*;

class LaboratoryResultRestMapperTest {
    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/";

    private final LaboratoryResultRestMapper mapper = Mappers.getMapper(LaboratoryResultRestMapper.class);

    @Test
    void shouldMapDataRequestToDomain() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();

        LaboratoryResult result = mapper.toLaboratoryResult(resultadoLaboratorioRequest);

        assertEquals("El mapeo del id de la orden es incorrecto", resultadoLaboratorioRequest.getIdOrden(), result.getOrderId());
        assertFalse("El mapeo de los archivos es incorrecto", result.getFiles().isEmpty());
    }

    @Test
    void shouldMapDomainToDataResponse() {

    }
}