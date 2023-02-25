package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.ResultadoLaboratorioResponse;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper
public interface LaboratoryResultMapper {
    LaboratoryResultMapper INTANCE = Mappers.getMapper(LaboratoryResultMapper.class);

    @Mapping(target="idOrder", source="idOrden")
    @Mapping(target="files", source="archivos")
    LaboratoryResult toLaboratoryResult(ResultadoLaboratorioRequest resultadoLaboratorioRequest);

    default ResultadoLaboratorioResponse toLaboratoryResponse(ResultadoLaboratorioRequest resultadoLaboratorioRequest) {
        String filesName = Arrays.stream(resultadoLaboratorioRequest.getArchivos())
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.joining(","));
        return ResultadoLaboratorioResponse.builder()
                .idOrden(resultadoLaboratorioRequest.getIdOrden())
                .archivos(filesName)
                .build();
    }
}
