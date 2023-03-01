package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.application.ports.input.LaboratoryResultUseCase;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.ResultadoLaboratorioResponse;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper.LaboratoryResultRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/resultados")
@RequiredArgsConstructor
public class LaboratoryResultAdapter {

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
}
