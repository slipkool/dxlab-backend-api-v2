package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "archivos")
public class ResultadoLaboratorioResponse {

    private Long idOrden;

    private String archivos;
}
