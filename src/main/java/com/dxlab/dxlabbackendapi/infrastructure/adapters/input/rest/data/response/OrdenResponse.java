package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenResponse {
    private Long id;
    private String usuario;
    private LocalDateTime fecha;
    private String examen;
    private boolean resultadoListo;
    private boolean muestraPendiente;
    private List<byte[]> galeriaDocumentos;
    private LocalDateTime fechaResultado;
}
