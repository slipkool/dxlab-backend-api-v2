package com.dxlab.dxlabbackendapi.domain.service;

import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.exception.NotFoundException;
import com.dxlab.dxlabbackendapi.domain.exception.OrderResultNotUpdate;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.domain.model.Order;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaboratoryResultServiceTest {
    private static final String USER_PEPITO_PEREZ = "Pepito perez";
    private static final String SRC_MAIN_RESOURCES_STATIC_TEST_PDF = "src/main/resources/testFiles/";

    @InjectMocks
    LaboratoryResultService laboratoryResultService;

    @Mock
    LaboratoryResultOutputport laboratoryResultOutputport;

    @Mock
    OrderOutputPort orderOutputPort;

    private final LaboratoryResultRestMapper mapper = Mappers.getMapper(LaboratoryResultRestMapper.class);

    @Test
    void shouldUploadLabResult_whenNoException() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();
        LaboratoryResult laboratoryResult = mapper.toLaboratoryResult(resultadoLaboratorioRequest);
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        doNothing().when(laboratoryResultOutputport).uploadFiles(any(LaboratoryResult.class));
        when(orderOutputPort.updateDateResultOrder(any(Order.class))).thenReturn(true);

        String result = assertDoesNotThrow(() -> {
            laboratoryResultService.uploadLabResult(laboratoryResult);
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldException_whenOrderNotFound() {
        LaboratoryResult laboratoryResult = LaboratoryResult.builder()
                .orderId(1L)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> laboratoryResultService.uploadLabResult(laboratoryResult));

        assertEquals("Orden no encontrada con el id: 1", exception.getMessage());
    }

    @Test
    void shouldException_whenNoUpdateDateResultOrder() throws IOException {
        Path path = Paths.get(SRC_MAIN_RESOURCES_STATIC_TEST_PDF + "Test.pdf");
        MockMultipartFile file = new MockMultipartFile("archivos", "Test.pdf", MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(path));
        MultipartFile[] multipartFiles = { file };
        ResultadoLaboratorioRequest resultadoLaboratorioRequest = ResultadoLaboratorioRequest.builder()
                .idOrden(1L)
                .archivos(multipartFiles)
                .build();
        LaboratoryResult laboratoryResult = mapper.toLaboratoryResult(resultadoLaboratorioRequest);
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        doNothing().when(laboratoryResultOutputport).uploadFiles(any(LaboratoryResult.class));
        when(orderOutputPort.updateDateResultOrder(any(Order.class))).thenReturn(false);

        OrderResultNotUpdate exception = assertThrows(OrderResultNotUpdate.class, () -> laboratoryResultService.uploadLabResult(laboratoryResult));

        assertEquals("No se pudo actualizar el resultado de laboratorio de la orden: 1", exception.getMessage());
    }

    @Test
    void shouldGetLabResultFileList() {
        LaboratoryResultInfo laboratoryResultInfoMock = LaboratoryResultInfo.builder()
                .orderId(1L)
                .nameFileList(List.of("Test.pdf"))
                .build();
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        when(laboratoryResultOutputport.getLabResultFileList(anyLong())).thenReturn(laboratoryResultInfoMock);

        LaboratoryResultInfo result = laboratoryResultService.getLabResultFileList(1L);

        assertEquals(laboratoryResultInfoMock.getOrderId(), result.getOrderId());
        assertFalse(result.getNameFileList().isEmpty());
    }

    @Test
    void shouldDeleteLabResultFile() {
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        doNothing().when(laboratoryResultOutputport).deleteLabResultFile(anyLong(), anyString());

        String result = assertDoesNotThrow(() -> {
            laboratoryResultService.deleteLabResultFile(1L, "Test.pdf");
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldDeleteLabResultFolder() {
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        doNothing().when(laboratoryResultOutputport).deleteLabResultFolder(anyLong());

        String result = assertDoesNotThrow(() -> {
            laboratoryResultService.deleteLabResultFolder(1L);
            return "OK";
        });

        assertEquals("OK", result);
    }

    @Test
    void shouldDownloadLabResultFile() {
        byte[] byteMock = "Test".getBytes();
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        when(laboratoryResultOutputport.downloadLabResultFile(anyLong(), anyString())).thenReturn(byteMock);

        byte[] result = laboratoryResultService.downloadLabResultFile(1L, "Test.pdf");
        assertArrayEquals(byteMock, result);
    }

    @Test
    void shouldDownloadZipLabResultFile() {
        byte[] byteMock = "Test".getBytes();
        Order orderMock = Order.builder()
                .id(1L)
                .user(USER_PEPITO_PEREZ)
                .build();
        when(orderOutputPort.getOrderById(anyLong())).thenReturn(Optional.of(orderMock));
        when(laboratoryResultOutputport.downloadZipLabResultFile(anyLong())).thenReturn(byteMock);

        byte[] result = laboratoryResultService.downloadZipLabResultFile(1L);
        assertArrayEquals(byteMock, result);
    }
}