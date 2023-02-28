package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest;

import com.dxlab.dxlabbackendapi.application.ports.input.LaboratoryResultUseCase;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.config.S3Properties;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.ResultadoLaboratorioResponse;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper.LaboratoryResultRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/resultados")
@RequiredArgsConstructor
public class LaboratoryResultAdapter {

    private final LaboratoryResultUseCase laboratoryResultUseCase;
    private final S3Properties properties;

    @PostMapping("/cargar-resultados")
    public ResponseEntity<ResultadoLaboratorioResponse> uploadLabResult(@ModelAttribute ResultadoLaboratorioRequest request) {
        request.validateSelf(properties.getMaxLengthFiles(),properties.getMaxFileSizeMb());
        LaboratoryResult laboratoryResult = LaboratoryResultRestMapper.INTANCE.toLaboratoryResult(request);

        laboratoryResultUseCase.uploadLabResult(laboratoryResult);

        URI location = URI.create(String.format("/cargar-resultados/%s", request.getIdOrden()));
        return ResponseEntity.created(location).body(LaboratoryResultRestMapper.INTANCE.toLaboratoryResponse(request));
    }
}
