package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenRequest {
    private Long id;
    @NotEmpty(message = "Usuario no debe ser vacio")
    private String usuario;
    private LocalDateTime fecha;
    @NotEmpty(message = "El examen no debe ser vacio")
    private String examen;
    private boolean resultadoListo;
    private boolean muestraPendiente;
    private LocalDateTime fechaResultado;
}
