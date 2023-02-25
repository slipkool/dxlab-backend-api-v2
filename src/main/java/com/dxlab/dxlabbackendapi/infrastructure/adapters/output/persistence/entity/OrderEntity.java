package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "usuario")
    private String user;
    @Column(name = "fecha", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private LocalDateTime date;
    @Column(name = "examen")
    private String examination;
    @Column(name = "resultado_listo", columnDefinition = "boolean default false")
    private Boolean readyResult;
    @Column(name = "muestra_pendiente", columnDefinition = "boolean default true")
    private Boolean samplePending;
    @Column(name = "fecha_resultado")
    private LocalDateTime dateResult;
}