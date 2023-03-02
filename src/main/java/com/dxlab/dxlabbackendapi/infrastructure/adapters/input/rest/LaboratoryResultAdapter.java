package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.application.ports.input.LaboratoryResultUseCase;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.ResultadoLaboratorioResponse;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper.LaboratoryResultRestMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

@RestController
@RequestMapping("/v1/resultados")
@RequiredArgsConstructor
public class LaboratoryResultAdapter {
    private static final String ZIP_FILENAME = "Resultados_orden_%s.zip";

    private final LaboratoryResultUseCase laboratoryResultUseCase;
    private final S3Properties properties;

    @PostMapping("/cargar")
    public ResponseEntity<ResultadoLaboratorioResponse> uploadLabResult(@ModelAttribute ResultadoLaboratorioRequest request) {
        request.validateSelf(properties.getMaxLengthFiles(),properties.getMaxFileSizeMb());
        LaboratoryResult laboratoryResult = LaboratoryResultRestMapper.INTANCE.toLaboratoryResult(request);

        laboratoryResultUseCase.uploadLabResult(laboratoryResult);

        URI location = URI.create(String.format("/cargar-resultados/%s", request.getIdOrden()));
        return ResponseEntity.created(location).body(LaboratoryResultRestMapper.INTANCE.toLaboratoryResponse(request));
    }

    @GetMapping("/listar/{idOrden}")
    public ResponseEntity<ResultadoLaboratorioResponse> getLabResultFileList(@PathVariable("idOrden") Long orderId) {
        LaboratoryResultInfo laboratoryResultInfo = laboratoryResultUseCase.getLabResultFileList(orderId);

        return ResponseEntity.ok().body(LaboratoryResultRestMapper.INTANCE.toLaboratoryResponse(laboratoryResultInfo));
    }

    @DeleteMapping("/eliminar/{idOrden}")
    public ResponseEntity<String> deleteLabResultFolder(@PathVariable("idOrden") Long orderId) {
        laboratoryResultUseCase.deleteLabResultFolder(orderId);

        return ResponseEntity.ok().body(String.format("Archivos de la orden %s, eliminados correctamente", orderId));
    }

    @DeleteMapping("/eliminar/{idOrden}/{nombreArchivo}")
    public ResponseEntity<String> deleteLabResultFile(@PathVariable("idOrden") Long orderId, @PathVariable("nombreArchivo") String fileName) {
        laboratoryResultUseCase.deleteLabResultFile(orderId, fileName);

        return ResponseEntity.ok().body(String.format("Archivo %s de la orden %s, eliminado correctamente", fileName, orderId));
    }

    @GetMapping("/descargar-zip/{idOrden}")
    public void downloadZipLabResultFile(@PathVariable("idOrden") Long orderId, HttpServletResponse response) throws IOException {
        response.setHeader("Content-type", "application-download");
        response.setHeader("Content-Disposition", "attachment; filename=" + String.format(ZIP_FILENAME, orderId));

        byte[] zipBytes = laboratoryResultUseCase.downloadZipLabResultFile(orderId);

        OutputStream outStream = response.getOutputStream();
        outStream.write(zipBytes);
        outStream.close();
        response.flushBuffer();
    }

    @GetMapping("/descargar/{idOrden}/{nombreArchivo}")
    public ResponseEntity<ByteArrayResource> downloadLabResultFile(@PathVariable("idOrden") Long orderId, @PathVariable("nombreArchivo") String fileName) {
        final byte[] data = laboratoryResultUseCase.downloadLabResultFile(orderId, fileName);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(data));
    }
}
