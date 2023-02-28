package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryFile;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.ResultadoLaboratorioRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.ResultadoLaboratorioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper
public interface LaboratoryResultRestMapper {
    LaboratoryResultRestMapper INTANCE = Mappers.getMapper(LaboratoryResultRestMapper.class);

    default LaboratoryResult toLaboratoryResult(ResultadoLaboratorioRequest resultadoLaboratorioRequest) {
        List<LaboratoryFile> laboratoryFiles = Arrays.stream(resultadoLaboratorioRequest.getArchivos())
                .map(LaboratoryResultRestMapper::getLaboratoryFile)
                .collect(Collectors.toList());
        return LaboratoryResult.builder()
                .idOrder(resultadoLaboratorioRequest.getIdOrden())
                .files(laboratoryFiles)
                .build();
    }

    private static LaboratoryFile getLaboratoryFile(MultipartFile file) {
        try {
            return LaboratoryFile.builder()
                    .fileName(Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_"))
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .inputStreamFile(file.getInputStream())
                    .build();
        } catch (IOException e) {
            throw new LaboratoryResultException("Error al mapear la información de archivos de la petición", e);
        }
    }

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
