package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoLaboratorioResponse {

    private Long idOrden;
    private List<String> listaNombreArchivo;
}
