package com.dxlab.dxlabbackendapi.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String user;
    private LocalDateTime date;
    private String examination;
    private boolean readyResult;
    private boolean samplePending;
    private LocalDateTime dateResult;
}
